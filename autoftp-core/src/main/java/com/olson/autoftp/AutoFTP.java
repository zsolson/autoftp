package com.olson.autoftp;

import com.olson.autoftp.gui.AutoFTPTrayIcon;
import com.olson.autoftp.gui.OptionsWindow;
import com.olson.autoftp.gui.RemoveFilesWindow;

public class AutoFTP {

    public AutoFTP() {
        settings = new Settings();
        settings.load("settings.conf");
        
        localDirectory = new LocalDirectory(settings.getLocalDirectoryPath());
        ftpDirectory = new FTPDirectory(settings.getFTPDirectoryAddress(),
                settings.getServerUsername(), settings.getServerPassword());
        merger = new Merger(localDirectory, ftpDirectory, new ChangeDetector());
        lastUpdate = System.currentTimeMillis();
        timePassed = settings.getTimeBetweenMerges();
        
        optionsWindow = new OptionsWindow(settings, localDirectory, ftpDirectory);
        removeFilesWindow = new RemoveFilesWindow(localDirectory, ftpDirectory);
        trayIcon = new AutoFTPTrayIcon(optionsWindow, removeFilesWindow);
    }
    
    public void run() {
        // TODO: timing isn't exact, it's about 6-8 seconds, NOT 5 like it should be
        
        while (trayIcon.isActive()) {            
            trayIcon.update();
            
            if (!trayIcon.isBusy()) {
                timePassed += (System.currentTimeMillis() - lastUpdate);
                lastUpdate = System.currentTimeMillis();
                
                if (timePassed >= settings.getTimeBetweenMerges()) {
                    // Don't merge if either the local directory or FTP directory
                    // are not enabled.
                    if (localDirectory.enabled() && ftpDirectory.enabled()) {
                        System.out.println("Merging...");

                        trayIcon.lock();
                        merger.merge();
                        removeFilesWindow.refreshTree();
                        trayIcon.unlock();

                        System.out.println("...end of merge.\n");

                        timePassed = 0;
                        lastUpdate = System.currentTimeMillis();
                    }
                }
            }
        }
    }
    
    public void dispose() {
        trayIcon.dispose();
        optionsWindow.dispose();
        removeFilesWindow.dispose();
        ftpDirectory.dispose();
    }
   

    // Application configuration related
    Settings settings;
    
    // Directory and merging relative objects
    private LocalDirectory localDirectory;
    private FTPDirectory ftpDirectory;
    private Merger merger;
    private long lastUpdate; // time when last update occured
    private long timePassed; // time passed since last merge
    
    // GUI related objects
    private AutoFTPTrayIcon trayIcon;
    private OptionsWindow optionsWindow;
    private RemoveFilesWindow removeFilesWindow;
            
    
    
    
    
    public static void main(String[] args) {
        try {
            AutoFTP program = new AutoFTP();
            program.run();
            program.dispose();
        } catch (Exception e) {
            System.err.println(e);
        }
        
        System.out.println("------------END------------");
    }

    /*
    private static void testDirectory(VirtualDirectory directory) throws IOException {        
        // Printing directory tree
        // WORKING WITH LOCAL DIRECTORY
        // WORKING WORKING WITH FTP DIRECTORY
        System.out.print(directory.getDirectoryTree());

        // Reading/adding files
        // WORKING WITH LOCAL DIRECTORY
        // WORKING WITH FTP DIRECTORY
        directory.addDirectory(new File("src"));
        directory.addDirectory(new File("src/win32"));
        directory.addDirectory(new File("include/win32"));
        
        // WORKING WITH LOCAL DIRECTORY
        // WORKING WITH FTP DIRECTORY
        InputStream stream = directory.getFileStream(new File("test.txt"));
        directory.addFile(new File("index.txt"), stream, 10);
        stream.close();
        stream = directory.getFileStream(new File("test.txt"));
        directory.addFile(new File("src/include/1/2/3/4/5/6/hello.txt"), stream, 10);
        stream.close();  
        stream = directory.getFileStream(new File("src/include/1/2/3/4/5/6/hello.txt"));
        directory.addFile(new File("documentation/byebye.txt"), stream, 10);
        stream.close();
        
        System.out.print(directory.getDirectoryTree());
        
        // Removing files
        // WORKING WITH LOCAL DIRECTORY
        // WORKING WITH FTP DIRECTORY
        directory.removeFile(new File("src"));
        directory.removeFile(new File("include/win32"));
        directory.removeFile(new File("include"));
        directory.removeFile(new File("documentation"));
        directory.removeFile(new File("index.txt"));
    }
    */
    
}
