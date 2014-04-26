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

public class AutoFTPTrayIcon
{
	private SystemTray m_systemTray;
	private TrayIcon m_trayIcon;
	private PopupMenu m_iconMenu;
	private PopupMenu m_disabledMenu;
	private MenuItem m_menuPauseResume;

	//private OptionsWindow m_optionsWindow;
	private RemoveFilesWindow m_removeFilesWindow;

	private boolean m_bActive;
	private boolean m_bBusy;
	private boolean m_bPaused;
	private boolean m_bLocked;

	public AutoFTPTrayIcon(/*OptionsWindow _optionsWindow, */RemoveFilesWindow _removeFilesWindow) throws RuntimeException
	{
		if (!SystemTray.isSupported())
			throw new RuntimeException("System tray is not supported on this platform!");

		m_systemTray = SystemTray.getSystemTray();
		try
		{
			Image image = Toolkit.getDefaultToolkit().getImage("src/main/resources/icons/tray_icon.png");

			MenuItem menuAbout = new MenuItem("About");
			menuAbout.addActionListener(new AboutListener());
			MenuItem menuOptions = new MenuItem("Options");
			menuOptions.addActionListener(new OptionsListener());
			MenuItem menuRemoveFiles = new MenuItem("Remove Files");
			menuRemoveFiles.addActionListener(new RemoveFilesListener());
			m_menuPauseResume = new MenuItem("Pause");
			m_menuPauseResume.addActionListener(new PauseResumeListener());
			MenuItem menuExit = new MenuItem("Exit");
			menuExit.addActionListener(new ExitListener());

			m_iconMenu = new PopupMenu();
			m_iconMenu.add(menuAbout);
			m_iconMenu.add(menuOptions);
			m_iconMenu.add(menuRemoveFiles);
			m_iconMenu.add(m_menuPauseResume);
			m_iconMenu.add(menuExit);

			m_disabledMenu = new PopupMenu();
			MenuItem disabledItem = new MenuItem("Merging...please wait");
			disabledItem.setEnabled(false);
			m_disabledMenu.add(disabledItem);

			m_trayIcon = new TrayIcon(image, "AutoFTP", m_iconMenu);

			m_systemTray.add(m_trayIcon);
		}
		catch (AWTException e)
		{
			System.err.println(e);
		}

		//m_optionsWindow = _optionsWindow;
		m_removeFilesWindow = _removeFilesWindow;

		m_bActive = true;
		m_bBusy = false;
		m_bPaused = false;
		m_bLocked = false;
	}

	public void update()
	{
		if (/*m_optionsWindow.isVisible() || */m_removeFilesWindow.isVisible())
		{
			m_bBusy = true;
		}
		else
		{
			m_bBusy = false;
		}
	}

	/*
	 * lock() and unlock() are called to prevent the tray icon from opening
	 * windows which could interfere with the merging process. So lock() is
	 * called just before starting a merge and unlock() is called just after.
	 */
	public void lock()
	{
		m_trayIcon.setPopupMenu(m_disabledMenu);
		m_bLocked = true;
	}

	public void unlock()
	{
		m_trayIcon.setPopupMenu(m_iconMenu);
		m_bLocked = false;
	}

	public boolean isActive()
	{
		return m_bActive;
	}

	public boolean isBusy()
	{
		return (m_bBusy || m_bPaused);
	}

	public void dispose()
	{
		m_systemTray.remove(m_trayIcon);
	}

	/* Event Handlers */
	private class AboutListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent _event)
		{
			boolean old = m_bBusy;
			m_bBusy = true;
			JOptionPane.showMessageDialog(null, "AutoFTP Version 0.2\nCreated by Donald Whyte", "About AutoFTP", JOptionPane.INFORMATION_MESSAGE);
			m_bBusy = old;
		}
	}

	private class OptionsListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent _event)
		{
			if (!m_bLocked)
			{
//				m_optionsWindow.setVisible(true);
			}
		}
	}

	private class RemoveFilesListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent _event)
		{
			if (!m_bLocked)
			{
				m_removeFilesWindow.showWindow();
			}
		}
	}

	private class PauseResumeListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent _event)
		{
			m_bPaused = !m_bPaused;
			if (m_bPaused)
				m_menuPauseResume.setLabel("Resume");
			else
				m_menuPauseResume.setLabel("Pause");
		}
	}

	private class ExitListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent _event)
		{
			m_bActive = false;
		}
	}

}
