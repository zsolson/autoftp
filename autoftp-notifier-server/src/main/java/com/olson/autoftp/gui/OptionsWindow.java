package com.olson.autoftp.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

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
import com.olson.autoftp.settings.ClientLocation;
import com.olson.autoftp.settings.ServerConnectionSettings;
import com.olson.autoftp.settings.SettingsLoader;
import com.olson.autoftp.settings.ClientConnectionSettings;

public class OptionsWindow extends JDialog
{
	private SettingsLoader m_settings;
	private ServerConnectionSettings m_serverSettings;
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

	private JPanel m_pushPortPanel;
	private JPanel m_clientSettingsPanel;
	private JPanel m_buttonPanel;

	private JTextField m_pushPortTextField;
	private Collection<ViewableClientSetting> m_listClientSettings = new LinkedList<ViewableClientSetting>();
	private GridLayout m_clientSettingsLayout;
	
	private class ViewableClientSetting
	{
		public JTextField m_clientAddressTextField;
		public JTextField m_clientPortTextField;
		
		public ViewableClientSetting(JPanel _parentPanel)
		{
			this (_parentPanel, "", "");
		}
		
		public ViewableClientSetting(JPanel _parentPanel, String _sClientAddress, String _sClientPort)
		{
			m_clientAddressTextField = new JTextField();
			m_clientAddressTextField.setMaximumSize(new Dimension(250, 20));
			m_clientAddressTextField.setText(_sClientAddress);
			m_clientPortTextField = new JTextField();
			m_clientPortTextField.setMaximumSize(new Dimension(250, 20));
			m_clientPortTextField.setText(_sClientPort);
			
			JPanel clientRow = new JPanel();
			clientRow.setLayout(new BoxLayout(clientRow, BoxLayout.LINE_AXIS));
			JLabel clientAddressLabel = new JLabel("Client Address: ");
			clientAddressLabel.setToolTipText("ip of the client computer.");
			clientRow.add(clientAddressLabel);
			clientRow.add(new Box.Filler(new Dimension(10, 0), new Dimension(10, 0), new Dimension(10, 0)));
			clientRow.add(m_clientAddressTextField);
			clientRow.add(new Box.Filler(new Dimension(20, 0), new Dimension(20, 0), new Dimension(20, 0)));
			JLabel clientPortLabel = new JLabel("Port: ");
			clientPortLabel.setToolTipText("port to use on the client computer.");
			clientRow.add(clientPortLabel);
			clientRow.add(new Box.Filler(new Dimension(10, 0), new Dimension(10, 0), new Dimension(10, 0)));
			clientRow.add(m_clientPortTextField);
			
			_parentPanel.add(clientRow);
			m_clientSettingsLayout.setColumns(m_listClientSettings.size() > 9 ? m_listClientSettings.size() + 1 : 10);
			pack();
		}
	}


	public OptionsWindow(SettingsLoader _settings, ServerConnectionSettings _serverSettings, LocalDirectory _localDirectory, FTPDirectory _ftpDirectory)
	{
		super(new JFrame(), "AutoFTP - Options", ModalityType.APPLICATION_MODAL);
		setVisible(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/resources/icons/tray_icon.png"));
		setLayout(new BorderLayout());

		// Creates all the widgets inside the window
		m_pushPortTextField = new JTextField();


		JPanel pushPortRow = new JPanel();
		pushPortRow.setLayout(new BoxLayout(pushPortRow, BoxLayout.LINE_AXIS));
		JLabel localDirectoryLabel = new JLabel("Push Data Port: ");
		pushPortRow.add(localDirectoryLabel);
		m_pushPortTextField.setMaximumSize(new Dimension(250, 20));
		pushPortRow.add(new Box.Filler(new Dimension(20, 0), new Dimension(20, 0), new Dimension(20, 0)));
		pushPortRow.add(m_pushPortTextField);

		m_pushPortPanel = new JPanel(new GridLayout(1, 0));
		m_pushPortPanel.setBorder(BorderFactory.createTitledBorder("Port Settings"));
		m_pushPortPanel.add(pushPortRow);
		
		m_clientSettingsLayout = new GridLayout(10, 0);
		m_clientSettingsPanel = new JPanel(m_clientSettingsLayout);
		m_clientSettingsPanel.setBorder(BorderFactory.createTitledBorder("Client Settings")); //TODO make this accept more than 10
		JButton addClientSettingsBtn = new JButton("Add Client Configuration");
		addClientSettingsBtn.addActionListener(new AddClientConfigButtonListener(m_clientSettingsPanel));
		addClientSettingsBtn.setPreferredSize(new Dimension(40, 40));
		
		m_clientSettingsPanel.add(addClientSettingsBtn);

		m_buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new OKButtonListener());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelButtonListener());
		m_buttonPanel.add(okButton);
		m_buttonPanel.add(cancelButton);

		// Adds all the necessary components to the frame
		add(m_pushPortPanel, BorderLayout.NORTH);
		add(m_clientSettingsPanel, BorderLayout.CENTER);
		add(m_buttonPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
		setSize(490, 500);
		setMinimumSize(new Dimension(490, 500));
		setResizable(false);
		// Centers the window
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);

		// Now store the given application settings and set appropriate defaults
		m_settings = _settings;
		m_serverSettings = _serverSettings;
		if (m_serverSettings != null)
		{
			m_pushPortTextField.setText(m_serverSettings.getNotifierServerPushPort());
			if (m_serverSettings.getClientLocations() != null)
			{
				for (ClientLocation clientLoc : m_serverSettings.getClientLocations())
				{
					ViewableClientSetting viewClientSetting = new ViewableClientSetting(m_clientSettingsPanel, clientLoc.getClientAddress(), clientLoc.getPullPort());
					m_listClientSettings.add(viewClientSetting);
				}
			}
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

			if (m_serverSettings == null)
				m_serverSettings = new ServerConnectionSettings();
			
			m_serverSettings.setNotifierServerPushPort(m_pushPortTextField.getText());
			Collection<ClientLocation> collClientLocs = new LinkedList<ClientLocation>();
			for (ViewableClientSetting viewClientSetting : m_listClientSettings)
				collClientLocs.add(new ClientLocation(viewClientSetting.m_clientAddressTextField.getText(), viewClientSetting.m_clientPortTextField.getText()));
			
			m_settings.save(m_serverSettings);
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

	private class AddClientConfigButtonListener implements ActionListener
	{
		private JPanel parent;

		public AddClientConfigButtonListener(JPanel _parent)
		{
			parent = _parent;
		}

		@Override
		public void actionPerformed(ActionEvent _event)
		{
			ViewableClientSetting viewClientSetting = new ViewableClientSetting(parent);
			m_listClientSettings.add(viewClientSetting);
		}

	}

}
