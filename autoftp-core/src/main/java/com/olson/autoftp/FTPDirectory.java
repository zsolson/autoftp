package com.olson.autoftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPDirectory implements VirtualDirectory {

    public FTPDirectory(String serverAddress, String username, String password) {
        this.serverAddress = serverAddress;
        this.username = username;
        this.password = password;
        
        ftp = new FTPClient();
        establishConnection();      
        
        String temp = Util.readFileAsString("text_extensions.conf");
        textExtensions = Arrays.asList(temp.split(" "));
        
        timer = new Timer();
        noopThread = new NoOpTask();
        timer.schedule(noopThread, 0, 10000); // send NOOP to server every 10 seconds
    }

    public void setAddress(String newAddress, String newUsername, String newPassword) {
        serverAddress = newAddress;
        username = newUsername;
        password = newPassword;
        endConnection();
        establishConnection();
    }
    
    /* Makes sure the program is disconnected from the FTP server and cleans
     * up the NOOP thread so it's not dangling. */
    public void dispose() {
        endConnection();
        noopThread.cancel();
        timer.cancel();
    }
    
    
    private void establishConnection() {
        try {
            if (!ftp.isConnected()) {                
                ftp.connect(serverAddress);
                ftp.login(username, password);
                
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                enabled = true;
            }
        } catch (IOException e) {
            enabled = false;
            System.err.println("Could not connect to FTP directory with address '" +
                serverAddress + "': " + e);
        }
    }
    
    private void endConnection() {
        try {
            ftp.disconnect();
        } catch (IOException e) {
            System.err.println("Error when disconnecting from server '" +
                serverAddress + "': " + e);
        }
    }
    
    /* Methods from interface implemented */
    @Override
    public boolean addFile(File file, InputStream content, long lastModifiedTime) {
        try {
            establishConnection();
            
            File parent = file.getParentFile();
            if (parent != null) {
                FTPFile ftpFile = ftp.mlistFile(Util.toUnixPath(parent.getPath()));
                if (ftpFile == null) {
                    addDirectory(parent);
                }
            }
            
            String path = Util.toUnixPath(file.getPath());
            boolean success = ftp.storeFile(path, content);
            
            // If file was uploaded successfully, change the timestamp to match the source's
            if (success) {
                FTPFile ftpFile = ftp.mlistFile(path);
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTimeInMillis(lastModifiedTime);
                ftpFile.setTimestamp(calendar);
            }
            
            return success;
        } catch (IOException e) {
            System.err.println("Failed to add file '" + file.getName() +
                "'to FTP directory '" + serverAddress + "': " + e);
            return false;
        }
    }
    
    @Override
    public boolean addDirectory(File directory) {
        try {
            establishConnection();
            
            File parent = directory.getParentFile();
            if (parent != null) {                
                FTPFile ftpFile = ftp.mlistFile(Util.toUnixPath(parent.getPath()));
                if (ftpFile == null) {
                    addDirectory(parent);
                }
            }
            
            String convertedPath = Util.toUnixPath(directory.getPath());
            return ftp.makeDirectory(convertedPath);
        } catch (IOException e) {
            System.err.println("Failed to add directory '" + directory.getName() +
                "'to FTP directory '" + serverAddress + "': " + e);
            return false;
        }
    }
    
    @Override
    public boolean removeFile(File file) {
        try {
            establishConnection();
            
            String convertedPath = Util.toUnixPath(file.getPath());
            
            FTPFile serverFile = ftp.mlistFile(convertedPath);
            if (serverFile == null) {
                return false;
            } else if (serverFile.isDirectory()) {
                for (FTPFile f : ftp.listFiles(convertedPath)) {
                    if (!(f.getName().equals(".") || f.getName().equals(".."))) {
                        removeFile(new File(convertedPath + "/" + f.getName()));
                    }
                }
                return ftp.removeDirectory(convertedPath);
            } else {
                return ftp.deleteFile(convertedPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to remove file '" + file.getName() +
                "'to FTP directory '" + serverAddress + "': " + e);
            return false;
        }
    }
    
    
    
    @Override
    public InputStream getFileStream(File file) {
        try {
            establishConnection();

            // This is here to check if the requested is file holds text data
            // and not binary data, changing the file type as appropriate.
            // Unfortunately, since there is no method for checking the encoding
            // of a file in FTPFile, we have to just make an educated guess by
            // checking the extension of the file.
            if (Util.isExtension(textExtensions, Util.getExtension(file))) {
                ftp.setFileType(FTP.ASCII_FILE_TYPE);
            }
            
            InputStream input = ftp.retrieveFileStream(Util.toUnixPath(file.getPath()));
            ftp.completePendingCommand();
            
            // Reads the stream in full, storing it another stream so the entire
            // file is stored in local memory
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            int b = input.read();
            while (b != -1) {
                temp.write(b);
                b = input.read();
            }
            
            InputStream toReturn = new ByteArrayInputStream(temp.toByteArray());
            
            return toReturn;
        } catch (IOException e) { 
            System.err.println("Failed to read file '" + file.getName() +
                "'from FTP directory '" + serverAddress + "': " + e);
            return null;
        } finally {
            try {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
    
    @Override
    public FileNode getDirectoryTree() {
        establishConnection();
        
        FileNode root = new FileNode("root", true, 0);
        try {
            for (FTPFile f : ftp.listFiles()) {
                FileNode child = createNode("", f);
                if (child != null) {
                    root.addChild(child);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not retrieve snapshot for FTP directory '"
                + serverAddress + "': " + e);
        }
        
        return root;
    }
    
    private FileNode createNode(String currentPath, FTPFile file) throws IOException {
        // If a file is a symbolic link, just FORGET ABOUT IT! Do not add it to
        // the directory's directory tree since it's not a real file/directory
        if (file.isSymbolicLink()) {
            return null;
        }
        
        String filename = file.getName();
        if (filename.equals(".") || filename.equals("..")) return null;
        
        // NOTE: Only add / if something is before it
        String fullPath = currentPath + ((currentPath.isEmpty()) ? "" : "/") + file.getName();
        FileNode node = new FileNode(fullPath, file.isDirectory(), file.getTimestamp().getTimeInMillis());
        
        if (file.isDirectory()) {
            for (FTPFile f : ftp.listFiles(fullPath)) {
                FileNode child = createNode(fullPath, f);
                if (child != null) {
                    node.addChild(child);
                }
            }
        }
            
        return node;
    }
    
    @Override
    public boolean enabled() {
        return enabled;
    }
    
    private String serverAddress;
    private String username;
    private String password;
    
    private FTPClient ftp;
    private List<String> textExtensions;
    private boolean enabled;
    
    // Used to occasionally send a NOOP message to the server to keep the connection alive
    private Timer timer;
    private TimerTask noopThread;

    private class NoOpTask extends TimerTask {
        @Override
        public void run() {
            try {
                ftp.sendNoOp();
            } catch (IOException e) {
                System.err.println("Could not send NOOP message to server: " + e);
            }
        }
    }
    
    
    
}
