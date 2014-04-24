package com.olson.autoftp;

import com.olson.autoftp.gui.AutoFTPTrayIcon;
import com.olson.autoftp.gui.OptionsWindow;
import com.olson.autoftp.gui.RemoveFilesWindow;
import com.olson.autoftp.settings.SettingsLoader;
import com.olson.autoftp.settings.ConnectionSettings;

public class AutoFTP
{
	// Application configuration related
	private SettingsLoader m_settings;

	// Directory and merging relative objects
	private LocalDirectory m_localDirectory;
	private FTPDirectory ftpDirectory;
	private Merger m_merger;
	private long m_lLastUpdate; // time when last update occured
	private long m_lTimePassed; // time passed since last merge

	// GUI related objects
	private AutoFTPTrayIcon m_trayIcon;
	private OptionsWindow m_optionsWindow;
	private RemoveFilesWindow m_removeFilesWindow;

	public AutoFTP()
	{
		m_settings = new SettingsLoader();
		ConnectionSettings settings = m_settings.load("settings.conf");
		m_optionsWindow = new OptionsWindow(m_settings, settings, m_localDirectory, ftpDirectory);
		while (settings == null)
		{
			m_optionsWindow.setVisible(true);
			settings = m_settings.load("settings.conf");
		}

		m_localDirectory = new LocalDirectory(settings.getLocalDirectoryPath());
		ftpDirectory = new FTPDirectory(settings.getFTPDirectoryAddress(), settings.getServerUsername(), settings.getServerPassword());
		m_merger = new Merger(m_localDirectory, ftpDirectory, new OldChangeDetector());
		m_lLastUpdate = System.currentTimeMillis();
//		m_lTimePassed = settings.getTimeBetweenMerges();

		m_optionsWindow = new OptionsWindow(m_settings, settings, m_localDirectory, ftpDirectory);
		m_removeFilesWindow = new RemoveFilesWindow(m_localDirectory, ftpDirectory);
		m_trayIcon = new AutoFTPTrayIcon(m_optionsWindow, m_removeFilesWindow);
	}

	public void run()
	{
		// TODO: timing isn't exact, it's about 6-8 seconds, NOT 5 like it
		// should be

		while (m_trayIcon.isActive())
		{
			m_trayIcon.update();

			if (!m_trayIcon.isBusy())
			{
				m_lTimePassed += (System.currentTimeMillis() - m_lLastUpdate);
				m_lLastUpdate = System.currentTimeMillis();

				//if (m_lTimePassed >= m_settings.getTimeBetweenMerges())
				{
					// Don't merge if either the local directory or FTP
					// directory
					// are not enabled.
					if (m_localDirectory.enabled() && ftpDirectory.enabled())
					{
						System.out.println("Merging...");

						m_trayIcon.lock();
						m_merger.merge();
						m_removeFilesWindow.refreshTree();
						m_trayIcon.unlock();

						System.out.println("...end of merge.\n");

						m_lTimePassed = 0;
						m_lLastUpdate = System.currentTimeMillis();
					}
				}
			}
		}
	}

	public void dispose()
	{
		m_trayIcon.dispose();
		m_optionsWindow.dispose();
		m_removeFilesWindow.dispose();
		ftpDirectory.dispose();
	}

	public static void main(String[] _args)
	{
		try
		{
			AutoFTP program = new AutoFTP();
			program.run();
			program.dispose();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}

		System.out.println("------------END------------");
	}

	/*
	 * private static void testDirectory(VirtualDirectory directory) throws
	 * IOException { // Printing directory tree // WORKING WITH LOCAL DIRECTORY
	 * // WORKING WORKING WITH FTP DIRECTORY
	 * System.out.print(directory.getDirectoryTree());
	 * 
	 * // Reading/adding files // WORKING WITH LOCAL DIRECTORY // WORKING WITH
	 * FTP DIRECTORY directory.addDirectory(new File("src"));
	 * directory.addDirectory(new File("src/win32")); directory.addDirectory(new
	 * File("include/win32"));
	 * 
	 * // WORKING WITH LOCAL DIRECTORY // WORKING WITH FTP DIRECTORY InputStream
	 * stream = directory.getFileStream(new File("test.txt"));
	 * directory.addFile(new File("index.txt"), stream, 10); stream.close();
	 * stream = directory.getFileStream(new File("test.txt"));
	 * directory.addFile(new File("src/include/1/2/3/4/5/6/hello.txt"), stream,
	 * 10); stream.close(); stream = directory.getFileStream(new
	 * File("src/include/1/2/3/4/5/6/hello.txt")); directory.addFile(new
	 * File("documentation/byebye.txt"), stream, 10); stream.close();
	 * 
	 * System.out.print(directory.getDirectoryTree());
	 * 
	 * // Removing files // WORKING WITH LOCAL DIRECTORY // WORKING WITH FTP
	 * DIRECTORY directory.removeFile(new File("src")); directory.removeFile(new
	 * File("include/win32")); directory.removeFile(new File("include"));
	 * directory.removeFile(new File("documentation")); directory.removeFile(new
	 * File("index.txt")); }
	 */

}
