package com.olson.autoftp.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public class AutoFTPTrayIcon {
    
    public AutoFTPTrayIcon(OptionsWindow optionsWindow, RemoveFilesWindow removeFilesWindow) throws RuntimeException {
        if (!SystemTray.isSupported()) 
            throw new RuntimeException("System tray is not supported on this platform!");
        
        systemTray = SystemTray.getSystemTray();
        try {
            Image image = Toolkit.getDefaultToolkit().getImage("icon.png");
            
            MenuItem menuAbout = new MenuItem("About");
            menuAbout.addActionListener(new AboutListener());
            MenuItem menuOptions = new MenuItem("Options");
            menuOptions.addActionListener(new OptionsListener());
            MenuItem menuRemoveFiles = new MenuItem("Remove Files");
            menuRemoveFiles.addActionListener(new RemoveFilesListener());
            menuPauseResume = new MenuItem("Pause");
            menuPauseResume.addActionListener(new PauseResumeListener());
            MenuItem menuExit = new MenuItem("Exit");
            menuExit.addActionListener(new ExitListener());
            
            iconMenu = new PopupMenu();
            iconMenu.add(menuAbout);
            iconMenu.add(menuOptions);
            iconMenu.add(menuRemoveFiles);
            iconMenu.add(menuPauseResume);
            iconMenu.add(menuExit);
                    
            disabledMenu = new PopupMenu();
            MenuItem disabledItem = new MenuItem("Merging...please wait");
            disabledItem.setEnabled(false);
            disabledMenu.add(disabledItem);
                    
            trayIcon = new TrayIcon(image, "AutoFTP", iconMenu);
            
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println(e);
        }
        
        this.optionsWindow = optionsWindow;
        this.removeFilesWindow = removeFilesWindow;        
        
        active = true;
        busy = false;
        paused = false;
        locked = false;
    }
    
    public void update() {
        if (optionsWindow.isVisible() || removeFilesWindow.isVisible()) {
            busy = true;
        } else {
            busy = false;
        }
    }
    
    /* lock() and unlock() are called to prevent the tray icon from opening
     * windows which could interfere with the merging process. So lock() is
     * called just before starting a merge and unlock() is called just after. */
    public void lock() {
        trayIcon.setPopupMenu(disabledMenu);
        locked = true;
    }
    
    public void unlock() {
        trayIcon.setPopupMenu(iconMenu);
        locked = false;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean isBusy() {
        return (busy || paused);
    }
    
    public void dispose() {
        systemTray.remove(trayIcon);
    }

    private SystemTray systemTray;
    private TrayIcon trayIcon;  
    private PopupMenu iconMenu;
    private PopupMenu disabledMenu;
    private MenuItem menuPauseResume;
    
    private OptionsWindow optionsWindow;
    private RemoveFilesWindow removeFilesWindow;
    
    private boolean active;
    private boolean busy;
    private boolean paused;
    private boolean locked;
    

    
    
    /* Event Handlers */
    private class AboutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean old = busy;
            busy = true;
            JOptionPane.showMessageDialog(null, "AutoFTP Version 0.2\nCreated by Donald Whyte",
                "About AutoFTP", JOptionPane.INFORMATION_MESSAGE);
            busy = old;
        }
    }
    
    private class OptionsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!locked) {
                optionsWindow.setVisible(true);
            }
        }
    }
    
    private class RemoveFilesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!locked) {
                removeFilesWindow.showWindow();
            }
        }
    }
    
    private class PauseResumeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            paused = !paused;
            if (paused) menuPauseResume.setLabel("Resume");
            else menuPauseResume.setLabel("Pause");
        }
    }
    
    private class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            active = false;
        }
    }  
    
}
