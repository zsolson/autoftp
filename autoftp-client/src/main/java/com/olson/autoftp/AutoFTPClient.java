package com.olson.autoftp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.GregorianCalendar;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.olson.autoftp.gui.AutoFTPTrayIcon;
import com.olson.autoftp.gui.OptionsWindow;
import com.olson.autoftp.gui.RemoveFilesWindow;
import com.olson.autoftp.request.FilePullRequest;
import com.olson.autoftp.settings.ClientConnectionSettings;
import com.olson.autoftp.settings.SettingsLoader;

public class AutoFTPClient {
	// Application configuration related
	private SettingsLoader m_settings;

	// Directory and merging relative objects
	private LocalDirectory m_localDirectory;
	private FTPDirectory m_ftpDirectory;
	private Merger m_merger;
	private long m_lLastUpdate; // time when last update occured
	private long m_lTimePassed; // time passed since last merge

	private ServerSocket m_localSocketServer;
	private Socket m_localSocket;
	private PrintWriter m_responder;
	private DataInputStream m_listener;
	private int m_iPortNumber;

	// GUI related objects
	private AutoFTPTrayIcon m_trayIcon;
	private OptionsWindow m_optionsWindow;
	private RemoveFilesWindow m_removeFilesWindow;
	
	private Kryo m_serializer;

	public AutoFTPClient() 
	{
		m_serializer = new Kryo();
		m_serializer.setClassLoader(AutoFTPClient.class.getClassLoader());
		m_settings = new SettingsLoader();
		ClientConnectionSettings settings = m_settings.load("settings.conf");
		m_optionsWindow = new OptionsWindow(m_settings, settings,
				m_localDirectory, m_ftpDirectory);
		while (!init(settings)) {
			m_optionsWindow.setVisible(true);
			settings = m_settings.load("settings.conf");
		}

		m_localDirectory = new LocalDirectory(settings.getLocalDirectoryPath());
		m_ftpDirectory = new FTPDirectory(settings.getFTPDirectoryAddress(),
				settings.getServerUsername(), settings.getServerPassword());
		m_merger = new Merger(m_localDirectory, m_ftpDirectory,
				new OldChangeDetector());
		m_lLastUpdate = System.currentTimeMillis();
		// m_lTimePassed = settings.getTimeBetweenMerges();

//		m_optionsWindow = new OptionsWindow(m_settings, settings,
//				m_localDirectory, m_ftpDirectory);
		m_removeFilesWindow = new RemoveFilesWindow(m_localDirectory,
				m_ftpDirectory);
		m_trayIcon = new AutoFTPTrayIcon(m_optionsWindow, m_removeFilesWindow);
	}

	private boolean init(ClientConnectionSettings _settings) {
		if (_settings == null)
			return false;

		if (Util.isEmpty(_settings.getLocalDirectoryPath())
				|| Util.isEmpty(_settings.getFTPDirectoryAddress())
				|| Util.isEmpty(_settings.getServerUsername())
				|| Util.isEmpty(_settings.getServerPassword())
				|| Util.isEmpty(_settings.getNotifierServerAddress())
				|| Util.isEmpty(_settings.getNotifierLocalPullPort())
				|| Util.isEmpty(_settings.getNotifierServerPushPort()))
			return false;

		m_localDirectory = new LocalDirectory(_settings.getLocalDirectoryPath());
		if (!m_localDirectory.exists())
			return false;

		m_ftpDirectory = new FTPDirectory(/*_settings.getFTPDirectoryAddress()*/"localhost", _settings.getServerUsername(), _settings.getServerPassword());

		m_iPortNumber = Integer.parseInt(_settings.getNotifierLocalPullPort());

//		try 
//		{
//			m_localSocketServer = new ServerSocket(portNumber);
//			m_localSocket = m_localSocketServer.accept();
////			m_localSocket = new Socket((String)null, portNumber);
//
////			SocketChannel socketChannel = SocketChannel.open();
////			boolean bConnected = socketChannel.connect(new InetSocketAddress("localhost", portNumber));
////			if (!bConnected)
////				return false;
////			socketChannel.configureBlocking(false);
////			socketChannel.re
////			socketChannel.bind(local)
//			m_responder = new PrintWriter(m_localSocket.getOutputStream(), true);
//			m_listener = new DataInputStream(m_localSocket.getInputStream());
//		} 
//		catch (IOException e) 
//		{
//
//			e.printStackTrace();
//			try 
//			{
//				m_localSocket.close();
//				m_localSocketServer.close();
//			}
//			catch (IOException e1) 
//			{
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			return false;
//		}
		return true;
	}

	public void run() {
		// TODO: timing isn't exact, it's about 6-8 seconds, NOT 5 like it
		// should be

		while (m_trayIcon.isActive()) {
			
			try {
				try 
				{
					m_localSocketServer = new ServerSocket(m_iPortNumber);
					m_localSocket = m_localSocketServer.accept();
//					m_localSocket = new Socket((String)null, portNumber);

//					SocketChannel socketChannel = SocketChannel.open();
//					boolean bConnected = socketChannel.connect(new InetSocketAddress("localhost", portNumber));
//					if (!bConnected)
//						return false;
//					socketChannel.configureBlocking(false);
//					socketChannel.re
//					socketChannel.bind(local)
					m_responder = new PrintWriter(m_localSocket.getOutputStream(), true);
					m_listener = new DataInputStream(m_localSocket.getInputStream());
				} 
				catch (IOException e) 
				{

					e.printStackTrace();
					try 
					{
						m_localSocket.close();
						m_localSocketServer.close();
					}
					catch (IOException e1) 
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
//					return false;
				}
				
				ByteArrayOutputStream data = new ByteArrayOutputStream();
				int b = m_listener.read();
				while (b != -1)
				{
					data.write(b);
					b = m_listener.read();
				}
				if (data.size() == 0)
					continue;
				FilePullRequest pullRequest = m_serializer.readObject(new Input(data.toByteArray()), FilePullRequest.class);
				if (pullRequest != null && pullRequest.getFileNamesToPull() != null)
				{
					m_ftpDirectory.connect();
					if (!m_ftpDirectory.exists())
					{
						//TODO log error message here
					}
					for (String sRemoteFileName : pullRequest.getFileNamesToPull())
					{
						File newFile = new File(sRemoteFileName);
						InputStream remoteFileData = m_ftpDirectory.getFileStream(newFile);
						m_localDirectory.addFile(newFile, remoteFileData, GregorianCalendar.getInstance().getTimeInMillis());
					}
					m_localSocket.close();
					m_localSocketServer.close();
				}
				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			m_trayIcon.update();
//
//			if (!m_trayIcon.isBusy()) {
//				m_lTimePassed += (System.currentTimeMillis() - m_lLastUpdate);
//				m_lLastUpdate = System.currentTimeMillis();
//
//				// if (m_lTimePassed >= m_settings.getTimeBetweenMerges())
//				{
//					// Don't merge if either the local directory or FTP
//					// directory
//					// are not enabled.
//					if (m_localDirectory.exists() && ftpDirectory.exists()) {
//						System.out.println("Merging...");
//
//						m_trayIcon.lock();
//						m_merger.merge();
//						m_removeFilesWindow.refreshTree();
//						m_trayIcon.unlock();
//
//						System.out.println("...end of merge.\n");
//
//						m_lTimePassed = 0;
//						m_lLastUpdate = System.currentTimeMillis();
//					}
//				}
//			}
		}
	}

	public void dispose() {
		m_trayIcon.dispose();
		m_optionsWindow.dispose();
		m_removeFilesWindow.dispose();
		m_ftpDirectory.dispose();
		try 
		{
			m_localSocket.close();
			m_localSocketServer.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] _args) {
		try {
			AutoFTPClient program = new AutoFTPClient();
			program.run();
			program.dispose();
		} catch (Exception e) {
			e.printStackTrace();
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
