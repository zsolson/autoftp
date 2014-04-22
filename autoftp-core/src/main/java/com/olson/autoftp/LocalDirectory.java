package com.olson.autoftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LocalDirectory implements VirtualDirectory {
	
    public LocalDirectory(String directory) {
        setDirectory(directory);
    }

    @Override
    public boolean addFile(File file, InputStream content, long lastModifiedTime) {
        try {
            File actualFile = new File(rootDirectory + file.getPath());
            
            // Check if the parent file (which is actually a directory) exists.
            // If it doesn't, then create it using addDirectory().
            File parent = new File(actualFile.getParent());
            if (!parent.exists()) {
                addParentDirectory(parent);
            }

            OutputStream output = new FileOutputStream(actualFile);
            int b = content.read();
            while (b != -1) {
                output.write(b);
                b = content.read();
            }
            output.close();
            
            // After adding the file, set the last modified time to whatever was given
            actualFile.setLastModified(lastModifiedTime);
                
            return true;
	} catch (IOException e) {
            System.err.println("Failed to add file '" + file.getName() +
                "'to local directory '" + rootDirectory + "': " + e);
            return false;
	}
    }
    
    public void setDirectory(String newDirectory) {
        rootDirectory = newDirectory;
        enabled = (new File(rootDirectory).exists());
    }
    
    /* Used by addFile to add a file's parent directory(s) if they don't exist. */
    private boolean addParentDirectory(File directory) {
        return directory.mkdirs();
    }
    
    @Override
    public boolean addDirectory(File directory) {
        File actualFile = new File(rootDirectory + directory.getPath());
        return actualFile.mkdirs();
    }

    @Override
    public boolean removeFile(File file) {
        File actualFile = new File(rootDirectory + file.getPath());
        if (actualFile.isDirectory()) {
            for (File f : actualFile.listFiles()) {
                removeNestedFile(f);
            }
        }
        return actualFile.delete();
    }
    
    private void removeNestedFile(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                removeNestedFile(f);
            }
        }
        file.delete();
    }

    @Override
    public InputStream getFileStream(File file) {
        try {
            File actualFile = new File(rootDirectory + file.getPath());
            
            if (actualFile.isDirectory()) {
                return null;
            } else {
                InputStream input = new FileInputStream(actualFile);
                return input;
            }
        } catch (IOException e) {
            System.err.println("Failed to read file '" + file.getName() +
                "'from local directory '" + rootDirectory + "': " + e);
            return null;
        }
    }

    @Override
    public FileNode getDirectoryTree() {
        File rootFile = new File(rootDirectory);
        FileNode rootNode = new FileNode("root", true, 0);
        
        // Ensures NullPointer error doesn't occur below
        if (!rootFile.exists()) return rootNode;
        
        for (File f : rootFile.listFiles()) {
            rootNode.addChild(createNode(f));
        }
        
	return rootNode;
    }
    
    private FileNode createNode(File file) {
        // Makes the file's path relative to the root directory
        String path = file.getPath();
        path = path.substring(rootDirectory.length());
        // Finally, makes sure that the path stored has been converted to
        // an UNIX path before being stored in the node        
        path = Util.toUnixPath(path);
        
        FileNode node = new FileNode(path, file.isDirectory(), file.lastModified());
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                node.addChild(createNode(f));
            }
        }
        return node;
    }
    
    @Override
    public boolean enabled() {
        return enabled;
    }

    private String rootDirectory;
    private boolean enabled;

}
