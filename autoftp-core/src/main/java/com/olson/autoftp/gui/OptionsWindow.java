package com.olson.autoftp.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.olson.autoftp.FTPDirectory;
import com.olson.autoftp.LocalDirectory;
import com.olson.autoftp.Settings;
import com.olson.autoftp.Util;

public class OptionsWindow extends JFrame {
    
    public OptionsWindow(Settings settings, LocalDirectory localDirectory, FTPDirectory ftpDirectory) {
        super("AutoFTP - Options");
        setVisible(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        setLayout(new BorderLayout());    
        
        // Creates all the widgets inside the window
        localDirectoryBox = new JTextField();
        ftpAddressBox = new JTextField();
        usernameBox = new JTextField();
        passwordBox = new JPasswordField();
        
        timeHoursBox = new JSpinner();
        timeHoursBox.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        timeHoursBox.setSize(90, 32);
        timeMinutesBox = new JSpinner();
        timeMinutesBox.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        timeSecondsBox = new JSpinner();
        timeSecondsBox.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        
        JLabel tempLabel = null;
        
        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.LINE_AXIS));
        tempLabel = new JLabel("Local Directory: ");
        tempLabel.setToolTipText("Full path to the local directory.");
        row1.add(tempLabel);
        localDirectoryBox.setMaximumSize(new Dimension(250, 20));
        row1.add(new Box.Filler(new Dimension(20, 0), new Dimension(20, 0), new Dimension(20, 0)));
        row1.add(localDirectoryBox);
        row1.add(new Box.Filler(new Dimension(10, 0), new Dimension(10, 0), new Dimension(10, 0)));
        JButton browseButton = new JButton("Browse");
        browseButton.setMaximumSize(new Dimension(80, 20));
        browseButton.addActionListener(new BrowseButtonListener(this));
        row1.add(browseButton);
        
        JPanel row2 = new JPanel();
        row2.setLayout(new BoxLayout(row2, BoxLayout.LINE_AXIS));
        tempLabel = new JLabel("Server Address:");
        tempLabel.setToolTipText("Address of the server that'll be communicating with the local directory.");
        row2.add(tempLabel);
        row2.add(new Box.Filler(new Dimension(20, 0), new Dimension(20, 0), new Dimension(20, 0)));
        ftpAddressBox.setMaximumSize(new Dimension(200, 20));
        row2.add(ftpAddressBox);
        
        JPanel row3 = new JPanel();
        row3.setLayout(new BoxLayout(row3, BoxLayout.LINE_AXIS));
        tempLabel = new JLabel("Username: ");
        tempLabel.setToolTipText("Username that will be used to communicate with the server.");
        row3.add(tempLabel);
        row3.add(new Box.Filler(new Dimension(47, 0), new Dimension(47, 0), new Dimension(47, 0)));
        usernameBox.setMaximumSize(new Dimension(200, 20));
        row3.add(usernameBox);
        
        JPanel row4 = new JPanel();
        row4.setLayout(new BoxLayout(row4, BoxLayout.LINE_AXIS));
        tempLabel = new JLabel("Password: ");
        tempLabel.setToolTipText("Password of the username specified.");
        row4.add(tempLabel);
        row4.add(new Box.Filler(new Dimension(47, 0), new Dimension(47, 0), new Dimension(47, 0)));
        passwordBox.setMaximumSize(new Dimension(200, 20));
        row4.add(passwordBox);
        
        panel = new JPanel(new GridLayout(4, 0));
        panel.setSize(0, 300);
        panel.setBorder(BorderFactory.createTitledBorder("Directory Settings"));
        panel.add(row1);
        panel.add(row2);
        panel.add(row3);
        panel.add(row4);
        panel.setMaximumSize(new Dimension(200, 290));
     
        panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createTitledBorder("Time Between Merges"));
        panel2.setToolTipText("The APPROXIMATE frequency both directories are merged.");
        panel2.add(timeHoursBox);
        panel2.add(new JLabel("hours"));
        panel2.add(timeMinutesBox);
        panel2.add(new JLabel("minutes"));
        panel2.add(timeSecondsBox);
        panel2.add(new JLabel("seconds"));
        
        panel3 = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new OKButtonListener());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CancelButtonListener());
        panel3.add(okButton);
        panel3.add(cancelButton);
        
        // Adds all the necessary components to the frame
        add(panel, BorderLayout.NORTH);
        add(panel2, BorderLayout.CENTER);
        add(panel3, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        setSize(490, 250);
        setResizable(false);
        // Centers the window
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
        
        // Now store the given application settings and set appropriate defaults
        this.settings = settings;
        localDirectoryBox.setText(settings.getLocalDirectoryPath());
        ftpAddressBox.setText(settings.getFTPDirectoryAddress());
        usernameBox.setText(settings.getServerUsername());
        passwordBox.setText(settings.getServerPassword());
        // Deconstruct milliseconds in hours, minutes and seconds
        long milliseconds = settings.getTimeBetweenMerges();
        int hours = (int)(milliseconds / 3600000);
        milliseconds %= 3600000;
        int minutes = (int)(milliseconds / 60000);
        milliseconds %= 60000;
        int seconds = (int)(milliseconds / 1000);
        timeHoursBox.setValue(hours);
        timeMinutesBox.setValue(minutes);
        timeSecondsBox.setValue(seconds);
        
        this.localDirectory = localDirectory;
        this.ftpDirectory = ftpDirectory;
    }
    
    private Settings settings;
    // Stored here so the window can update their settings when the Sabe button is clicked
    private LocalDirectory localDirectory;
    private FTPDirectory ftpDirectory;
    
    private JPanel panel;
    private JPanel panel2;
    private JPanel panel3;
            
    private JTextField localDirectoryBox;
    private JTextField ftpAddressBox;
    private JTextField usernameBox;
    private JPasswordField passwordBox;
    
    private JSpinner timeHoursBox;
    private JSpinner timeMinutesBox;
    private JSpinner timeSecondsBox;
    
    private class OKButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean localDirChanged = !localDirectoryBox.getText().equals(settings.getLocalDirectoryPath());
            boolean ftpDetailsChanged = !(ftpAddressBox.getText().equals(settings.getFTPDirectoryAddress())
            && usernameBox.getText().equals(settings.getServerUsername())
            && passwordBox.getText().equals(settings.getServerPassword())
            );
            
            if (localDirChanged) {
                settings.setLocalDirectoryPath(localDirectoryBox.getText());
                localDirectory.setDirectory(settings.getLocalDirectoryPath());
            }
            if (ftpDetailsChanged) {
                settings.setFTPDirectoryAddress(ftpAddressBox.getText());
                settings.setServerUsername(usernameBox.getText());
                settings.setServerPassword(passwordBox.getText());
                // TODO: test ftp directory update testing
                ftpDirectory.setAddress(settings.getFTPDirectoryAddress(),
                    settings.getServerUsername(), settings.getServerPassword());
            }
            // Calculates the milliseconds of the user's chosen time
            long milliseconds = 0;
            milliseconds += 3600000 * (Integer)timeHoursBox.getValue();
            milliseconds += 60000 * (Integer)timeMinutesBox.getValue();
            milliseconds += 1000 * (Integer)timeSecondsBox.getValue();
            settings.setTimeBetweenMerges(milliseconds);

            settings.save("settings.conf");
            setVisible(false);
        }
    }
    
    private class CancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    }
    
    private class BrowseButtonListener implements ActionListener {
        public BrowseButtonListener(Frame parent) {
            this.parent = parent;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser dialog = new JFileChooser();
            dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dialog.showDialog(parent, "Select");
            
            File selectedDirectory = dialog.getSelectedFile();
            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                String path = Util.toUnixPath(selectedDirectory.getPath()) + "/";
                localDirectoryBox.setText(path);
            }
                
        }
        
        private Frame parent;
        
    }
    
}
