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
import com.olson.autoftp.settings.ClientConnectionSettings;

public class OptionsWindow extends JDialog
{
	private SettingsLoader m_settings;
	private ClientConnectionSettings m_clientSettings;
	// Stored here so the window can update their settings when the Sabe button
	// is clicked
	private LocalDirectory m_localDirectory;
	private FTPDirectory m_ftpDirectory;
	
//	private String m_sLocalDirectoryPath;
//	private String m_sFTPDirectoryAddress;
//	private String m_sServerUsername;
//	private String m_sServerPassword;
//	private String m_sNotifierServerAddress;
//	private String m_sNotifierLocalPullPort;
//	private String m_sNotifierServerPushPort;

	private JPanel m_localDirPanel;
	private JPanel m_ftpSettingsPanel;
	private JPanel m_notifierServerSettingsPanel;
	private JPanel m_serverSettingsPanel;
	private JPanel m_buttonPanel;

	private JTextField m_localDirectoryTextField;
	private JTextField m_ftpAddressTextField;
	private JTextField m_serverUsernameTextField;
	private JPasswordField m_serverPasswordTextField;
	private JTextField m_notifierServerAddressTextField;
	private JTextField m_notifierServerPullPortTextField;
	private JTextField m_notifierServerPushPortTextField;
	


	public OptionsWindow(SettingsLoader _settings, ClientConnectionSettings _clientSettings, LocalDirectory _localDirectory, FTPDirectory _ftpDirectory)
	{
		super(new JFrame(), "AutoFTP - Options", ModalityType.APPLICATION_MODAL);
		setVisible(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/resources/icons/tray_icon.png"));
		setLayout(new BorderLayout());

		// Creates all the widgets inside the window
		m_localDirectoryTextField = new JTextField();
		m_ftpAddressTextField = new JTextField();
		m_serverUsernameTextField = new JTextField();
		m_serverPasswordTextField = new JPasswordField();
		m_notifierServerAddressTextField = new JTextField();
		m_notifierServerPullPortTextField = new JTextField();
		m_notifierServerPushPortTextField = new JTextField();


		JPanel locaDirRow = new JPanel();
		locaDirRow.setLayout(new BoxLayout(locaDirRow, BoxLayout.LINE_AXIS));
		JLabel localDirectoryLabel = new JLabel("Local Directory: ");
		localDirectoryLabel.setToolTipText("Full path to the local directory.");
		locaDirRow.add(localDirectoryLabel);
		m_localDirectoryTextField.setMaximumSize(new Dimension(250, 20));
		locaDirRow.add(new Box.Filler(new Dimension(20, 0), new Dimension(20, 0), new Dimension(20, 0)));
		locaDirRow.add(m_localDirectoryTextField);
		locaDirRow.add(new Box.Filler(new Dimension(10, 0), new Dimension(10, 0), new Dimension(10, 0)));
		JButton browseButton = new JButton("Browse");
		browseButton.setMaximumSize(new Dimension(80, 20));
		browseButton.addActionListener(new BrowseButtonListener(this));
		locaDirRow.add(browseButton);

		JPanel ftpAddressRow = new JPanel();
		ftpAddressRow.setLayout(new BoxLayout(ftpAddressRow, BoxLayout.LINE_AXIS));
		JLabel ftpAddressLabel = new JLabel("Server Address:");
		ftpAddressLabel.setToolTipText("Address of the server that'll be communicating with the local directory.");
		ftpAddressRow.add(ftpAddressLabel);
		ftpAddressRow.add(new Box.Filler(new Dimension(20, 0), new Dimension(20, 0), new Dimension(20, 0)));
		m_ftpAddressTextField.setMaximumSize(new Dimension(200, 20));
		ftpAddressRow.add(m_ftpAddressTextField);

		JPanel ftpUsernameRow = new JPanel();
		ftpUsernameRow.setLayout(new BoxLayout(ftpUsernameRow, BoxLayout.LINE_AXIS));
		JLabel serverUsernameLabel = new JLabel("Username: ");
		serverUsernameLabel.setToolTipText("Username that will be used to communicate with the server.");
		ftpUsernameRow.add(serverUsernameLabel);
		ftpUsernameRow.add(new Box.Filler(new Dimension(47, 0), new Dimension(47, 0), new Dimension(47, 0)));
		m_serverUsernameTextField.setMaximumSize(new Dimension(200, 20));
		ftpUsernameRow.add(m_serverUsernameTextField);

		JPanel ftpPasswordRow = new JPanel();
		ftpPasswordRow.setLayout(new BoxLayout(ftpPasswordRow, BoxLayout.LINE_AXIS));
		JLabel serverPasswordLabel = new JLabel("Password: ");
		serverPasswordLabel.setToolTipText("Password of the username specified.");
		ftpPasswordRow.add(serverPasswordLabel);
		ftpPasswordRow.add(new Box.Filler(new Dimension(47, 0), new Dimension(47, 0), new Dimension(47, 0)));
		m_serverPasswordTextField.setMaximumSize(new Dimension(200, 20));
		ftpPasswordRow.add(m_serverPasswordTextField);

		JPanel notifierServerAddressRow = new JPanel();
		notifierServerAddressRow.setLayout(new BoxLayout(notifierServerAddressRow, BoxLayout.LINE_AXIS));
		JLabel notifierServerAddressLabel = new JLabel("Notifier Server Address:");
		notifierServerAddressRow.add(notifierServerAddressLabel);
		notifierServerAddressRow.add(new Box.Filler(new Dimension(20, 0), new Dimension(20, 0), new Dimension(20, 0)));
		m_notifierServerAddressTextField.setMaximumSize(new Dimension(200, 20));
		notifierServerAddressRow.add(m_notifierServerAddressTextField);

		JPanel notifierServerPullPortRow = new JPanel();
		notifierServerPullPortRow.setLayout(new BoxLayout(notifierServerPullPortRow, BoxLayout.LINE_AXIS));
		JLabel notifierServerPullPortLabel = new JLabel("Notifier Server Pull Request Port: ");
		notifierServerPullPortLabel.setToolTipText("Port that the Notifier Server will use to notify clients of new files to retrieve.");
		notifierServerPullPortRow.add(notifierServerPullPortLabel);
		notifierServerPullPortRow.add(new Box.Filler(new Dimension(47, 0), new Dimension(47, 0), new Dimension(47, 0)));
		m_notifierServerPullPortTextField.setMaximumSize(new Dimension(200, 20));
		notifierServerPullPortRow.add(m_notifierServerPullPortTextField);

		JPanel notifierServerPushPortRow = new JPanel();
		notifierServerPushPortRow.setLayout(new BoxLayout(notifierServerPushPortRow, BoxLayout.LINE_AXIS));
		JLabel notifierServerPushPortLabel = new JLabel("Notifier Server Push Request Port: ");
		notifierServerPushPortLabel.setToolTipText("Port that the Client will use to inform the Notifier Server of new files to be uploaded.");
		notifierServerPushPortRow.add(notifierServerPushPortLabel);
		notifierServerPushPortRow.add(new Box.Filler(new Dimension(47, 0), new Dimension(47, 0), new Dimension(47, 0)));
		m_notifierServerPushPortTextField.setMaximumSize(new Dimension(200, 20));
		notifierServerPushPortRow.add(m_notifierServerPushPortTextField);

		m_localDirPanel = new JPanel(new GridLayout(1, 0));
		m_localDirPanel.setBorder(BorderFactory.createTitledBorder("Local Directory Settings"));
		m_localDirPanel.add(locaDirRow);

		m_ftpSettingsPanel = new JPanel(new GridLayout(3, 0));
		m_ftpSettingsPanel.setBorder(BorderFactory.createTitledBorder("FTP Server Settings"));
		m_ftpSettingsPanel.add(ftpAddressRow);
		m_ftpSettingsPanel.add(ftpUsernameRow);
		m_ftpSettingsPanel.add(ftpPasswordRow);
		
		m_notifierServerSettingsPanel = new JPanel(new GridLayout(3, 0));
		m_notifierServerSettingsPanel.setBorder(BorderFactory.createTitledBorder("Notifier Server Settings"));
		m_notifierServerSettingsPanel.add(notifierServerAddressRow);
		m_notifierServerSettingsPanel.add(notifierServerPullPortRow);
		m_notifierServerSettingsPanel.add(notifierServerPushPortRow);
		
		m_serverSettingsPanel = new JPanel(new GridLayout(2, 0));
		m_serverSettingsPanel.add(m_ftpSettingsPanel);
		m_serverSettingsPanel.add(m_notifierServerSettingsPanel);

		m_buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new OKButtonListener());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelButtonListener());
		m_buttonPanel.add(okButton);
		m_buttonPanel.add(cancelButton);

		// Adds all the necessary components to the frame
		add(m_localDirPanel, BorderLayout.NORTH);
		add(m_serverSettingsPanel, BorderLayout.CENTER);
		add(m_buttonPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
		setSize(490, 500);
		setResizable(false);
		// Centers the window
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);

		// Now store the given application settings and set appropriate defaults
		m_settings = _settings;
		m_clientSettings = _clientSettings;
		if (m_clientSettings != null)
		{
			m_localDirectoryTextField.setText(m_clientSettings.getLocalDirectoryPath());
			m_ftpAddressTextField.setText(m_clientSettings.getFTPDirectoryAddress());
			m_serverUsernameTextField.setText(m_clientSettings.getServerUsername());
			m_serverPasswordTextField.setText(m_clientSettings.getServerPassword());
			m_notifierServerAddressTextField.setText(m_clientSettings.getNotifierServerAddress());
			m_notifierServerPullPortTextField.setText(m_clientSettings.getNotifierLocalPullPort());
			m_notifierServerPushPortTextField.setText(m_clientSettings.getNotifierServerPushPort());
		}
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

			if (m_clientSettings == null)
				m_clientSettings = new ClientConnectionSettings();
			
			m_clientSettings.setLocalDirectoryPath(m_localDirectoryTextField.getText());
			m_clientSettings.setFTPDirectoryAddress(m_ftpAddressTextField.getText());
			m_clientSettings.setServerUsername(m_serverUsernameTextField.getText());
			m_clientSettings.setServerPassword(String.valueOf(m_serverPasswordTextField.getPassword()));
			m_clientSettings.setNotifierServerAddress(m_notifierServerAddressTextField.getText());
			m_clientSettings.setNotifierLocalPullPort(m_notifierServerPullPortTextField.getText());
			m_clientSettings.setNotifierServerPushPort(m_notifierServerPushPortTextField.getText());
			m_settings.save("settings.conf", m_clientSettings);
			setVisible(false);
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
				m_localDirectoryTextField.setText(path);
			}

		}

	}

}
