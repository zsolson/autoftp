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
import javax.swing.JDialog;
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
import com.olson.autoftp.Util;
import com.olson.autoftp.settings.SettingsLoader;
import com.olson.autoftp.settings.ConnectionSettings;

public class OptionsWindow extends JDialog
{
	private SettingsLoader m_settings;
	// Stored here so the window can update their settings when the Sabe button
	// is clicked
	private LocalDirectory m_localDirectory;
	private FTPDirectory m_ftpDirectory;

	private JPanel m_panel;
	private JPanel m_panel2;
	private JPanel m_panel3;

	private JTextField m_localDirectoryBox;
	private JTextField m_ftpAddressBox;
	private JTextField m_usernameBox;
	private JPasswordField m_passwordBox;

	private JSpinner m_timeHoursBox;
	private JSpinner m_timeMinutesBox;
	private JSpinner m_timeSecondsBox;

	public OptionsWindow(SettingsLoader _settings, ConnectionSettings _userSettings, LocalDirectory _localDirectory, FTPDirectory _ftpDirectory)
	{
		super(new JFrame(), "AutoFTP - Options", ModalityType.APPLICATION_MODAL);
		setVisible(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/resources/icons/tray_icon.png"));
		setLayout(new BorderLayout());

		// Creates all the widgets inside the window
		m_localDirectoryBox = new JTextField();
		m_ftpAddressBox = new JTextField();
		m_usernameBox = new JTextField();
		m_passwordBox = new JPasswordField();

		m_timeHoursBox = new JSpinner();
		m_timeHoursBox.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		m_timeHoursBox.setSize(90, 32);
		m_timeMinutesBox = new JSpinner();
		m_timeMinutesBox.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		m_timeSecondsBox = new JSpinner();
		m_timeSecondsBox.setModel(new SpinnerNumberModel(0, 0, 59, 1));

		JLabel tempLabel = null;

		JPanel row1 = new JPanel();
		row1.setLayout(new BoxLayout(row1, BoxLayout.LINE_AXIS));
		tempLabel = new JLabel("Local Directory: ");
		tempLabel.setToolTipText("Full path to the local directory.");
		row1.add(tempLabel);
		m_localDirectoryBox.setMaximumSize(new Dimension(250, 20));
		row1.add(new Box.Filler(new Dimension(20, 0), new Dimension(20, 0), new Dimension(20, 0)));
		row1.add(m_localDirectoryBox);
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
		m_ftpAddressBox.setMaximumSize(new Dimension(200, 20));
		row2.add(m_ftpAddressBox);

		JPanel row3 = new JPanel();
		row3.setLayout(new BoxLayout(row3, BoxLayout.LINE_AXIS));
		tempLabel = new JLabel("Username: ");
		tempLabel.setToolTipText("Username that will be used to communicate with the server.");
		row3.add(tempLabel);
		row3.add(new Box.Filler(new Dimension(47, 0), new Dimension(47, 0), new Dimension(47, 0)));
		m_usernameBox.setMaximumSize(new Dimension(200, 20));
		row3.add(m_usernameBox);

		JPanel row4 = new JPanel();
		row4.setLayout(new BoxLayout(row4, BoxLayout.LINE_AXIS));
		tempLabel = new JLabel("Password: ");
		tempLabel.setToolTipText("Password of the username specified.");
		row4.add(tempLabel);
		row4.add(new Box.Filler(new Dimension(47, 0), new Dimension(47, 0), new Dimension(47, 0)));
		m_passwordBox.setMaximumSize(new Dimension(200, 20));
		row4.add(m_passwordBox);

		m_panel = new JPanel(new GridLayout(4, 0));
		m_panel.setSize(0, 300);
		m_panel.setBorder(BorderFactory.createTitledBorder("Directory Settings"));
		m_panel.add(row1);
		m_panel.add(row2);
		m_panel.add(row3);
		m_panel.add(row4);
		m_panel.setMaximumSize(new Dimension(200, 290));

		m_panel2 = new JPanel();
		m_panel2.setBorder(BorderFactory.createTitledBorder("Time Between Merges"));
		m_panel2.setToolTipText("The APPROXIMATE frequency both directories are merged.");
		m_panel2.add(m_timeHoursBox);
		m_panel2.add(new JLabel("hours"));
		m_panel2.add(m_timeMinutesBox);
		m_panel2.add(new JLabel("minutes"));
		m_panel2.add(m_timeSecondsBox);
		m_panel2.add(new JLabel("seconds"));

		m_panel3 = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new OKButtonListener());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelButtonListener());
		m_panel3.add(okButton);
		m_panel3.add(cancelButton);

		// Adds all the necessary components to the frame
		add(m_panel, BorderLayout.NORTH);
		add(m_panel2, BorderLayout.CENTER);
		add(m_panel3, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
		setSize(490, 250);
		setResizable(false);
		// Centers the window
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);

		// Now store the given application settings and set appropriate defaults
		m_settings = _settings;
		m_localDirectoryBox.setText(_userSettings.getLocalDirectoryPath());
		m_ftpAddressBox.setText(_userSettings.getFTPDirectoryAddress());
		m_usernameBox.setText(_userSettings.getServerUsername());
		m_passwordBox.setText(_userSettings.getServerPassword());
		// Deconstruct milliseconds in hours, minutes and seconds
		long milliseconds = 0;//_userSettings.getTimeBetweenMerges();
		int hours = (int) (milliseconds / 3600000);
		milliseconds %= 3600000;
		int minutes = (int) (milliseconds / 60000);
		milliseconds %= 60000;
		int seconds = (int) (milliseconds / 1000);
		m_timeHoursBox.setValue(hours);
		m_timeMinutesBox.setValue(minutes);
		m_timeSecondsBox.setValue(seconds);

		m_localDirectory = _localDirectory;
		m_ftpDirectory = _ftpDirectory;
	}

	private class OKButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent _event)
		{
//			boolean localDirChanged = !m_localDirectoryBox.getText().equals(m_settings.getLocalDirectoryPath());
//			boolean ftpDetailsChanged = !(m_ftpAddressBox.getText().equals(m_settings.getFTPDirectoryAddress())
//					&& m_usernameBox.getText().equals(m_settings.getServerUsername()) && m_passwordBox.getText().equals(m_settings.getServerPassword()));
//
//			if (localDirChanged)
//			{
//				m_settings.setLocalDirectoryPath(m_localDirectoryBox.getText());
//				m_localDirectory.setDirectory(m_settings.getLocalDirectoryPath());
//			}
//			if (ftpDetailsChanged)
//			{
//				m_settings.setFTPDirectoryAddress(m_ftpAddressBox.getText());
//				m_settings.setServerUsername(m_usernameBox.getText());
//				m_settings.setServerPassword(m_passwordBox.getText());
//				// TODO: test ftp directory update testing
//				m_ftpDirectory.setAddress(m_settings.getFTPDirectoryAddress(), m_settings.getServerUsername(), m_settings.getServerPassword());
//			}
//			// Calculates the milliseconds of the user's chosen time
//			long milliseconds = 0;
//			milliseconds += 3600000 * (Integer) m_timeHoursBox.getValue();
//			milliseconds += 60000 * (Integer) m_timeMinutesBox.getValue();
//			milliseconds += 1000 * (Integer) m_timeSecondsBox.getValue();
//			m_settings.setTimeBetweenMerges(milliseconds);
//
//			m_settings.save("settings.conf");
//			setVisible(false);
		}
	}

	private class CancelButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent _event)
		{
			setVisible(false);
		}
	}

	private class BrowseButtonListener implements ActionListener
	{
		private JDialog parent;

		public BrowseButtonListener(JDialog _parent)
		{
			parent = _parent;
		}

		@Override
		public void actionPerformed(ActionEvent _event)
		{
			JFileChooser dialog = new JFileChooser();
			dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			dialog.showDialog(parent, "Select");

			File selectedDirectory = dialog.getSelectedFile();
			if (selectedDirectory != null && selectedDirectory.isDirectory())
			{
				String path = Util.toUnixPath(selectedDirectory.getPath()) + "/";
				m_localDirectoryBox.setText(path);
			}

		}

	}

}
