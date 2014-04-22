package com.olson.autoftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPDirectory implements VirtualDirectory
{
	private String m_sServerAddress;
	private String m_sUsername;
	private String m_sPassword;

	private FTPClient m_ftpClient;
	private List<String> m_listTextExtensions;
	private boolean m_bEnabled;

	// Used to occasionally send a NOOP message to the server to keep the
	// connection alive
	private Timer m_timer;
	private TimerTask m_noopThread;

	public FTPDirectory(String _sServerAddress, String _sUsername, String _sPassword)
	{
		m_sServerAddress = _sServerAddress;
		m_sUsername = _sUsername;
		m_sPassword = _sPassword;

		m_ftpClient = new FTPClient();
		establishConnection();

		String temp = Util.readFileAsString("text_extensions.conf");
		m_listTextExtensions = Arrays.asList(temp.split(" "));

		m_timer = new Timer();
		m_noopThread = new NoOpTask();
		m_timer.schedule(m_noopThread, 0, 10000); // send NOOP to server every
													// 10
													// seconds
	}

	public void setAddress(String _sNewAddress, String _sNewUsername, String _sNewPassword)
	{
		m_sServerAddress = _sNewAddress;
		m_sUsername = _sNewUsername;
		m_sPassword = _sNewPassword;
		endConnection();
		establishConnection();
	}

	/*
	 * Makes sure the program is disconnected from the FTP server and cleans up
	 * the NOOP thread so it's not dangling.
	 */
	public void dispose()
	{
		endConnection();
		m_noopThread.cancel();
		m_timer.cancel();
	}

	private void establishConnection()
	{
		try
		{
			if (!m_ftpClient.isConnected())
			{
				m_ftpClient.connect(m_sServerAddress);
				m_ftpClient.login(m_sUsername, m_sPassword);

				m_ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				m_bEnabled = true;
			}
		}
		catch (IOException e)
		{
			m_bEnabled = false;
			System.err.println("Could not connect to FTP directory with address '" + m_sServerAddress + "': " + e);
		}
	}

	private void endConnection()
	{
		try
		{
			m_ftpClient.disconnect();
		}
		catch (IOException e)
		{
			System.err.println("Error when disconnecting from server '" + m_sServerAddress + "': " + e);
		}
	}

	/* Methods from interface implemented */
	@Override
	public boolean addFile(File _file, InputStream _contentStream, long _lLastModifiedTime)
	{
		try
		{
			establishConnection();

			File parent = _file.getParentFile();
			if (parent != null)
			{
				FTPFile ftpFile = m_ftpClient.mlistFile(Util.toUnixPath(parent.getPath()));
				if (ftpFile == null)
				{
					addDirectory(parent);
				}
			}

			String path = Util.toUnixPath(_file.getPath());
			boolean success = m_ftpClient.storeFile(path, _contentStream);

			// If file was uploaded successfully, change the timestamp to match
			// the source's
			if (success)
			{
				FTPFile ftpFile = m_ftpClient.mlistFile(path);
				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTimeInMillis(_lLastModifiedTime);
				ftpFile.setTimestamp(calendar);
			}

			return success;
		}
		catch (IOException e)
		{
			System.err.println("Failed to add file '" + _file.getName() + "'to FTP directory '" + m_sServerAddress + "': " + e);
			return false;
		}
	}

	@Override
	public boolean addDirectory(File _directory)
	{
		try
		{
			establishConnection();

			File parent = _directory.getParentFile();
			if (parent != null)
			{
				FTPFile ftpFile = m_ftpClient.mlistFile(Util.toUnixPath(parent.getPath()));
				if (ftpFile == null)
				{
					addDirectory(parent);
				}
			}

			String convertedPath = Util.toUnixPath(_directory.getPath());
			return m_ftpClient.makeDirectory(convertedPath);
		}
		catch (IOException e)
		{
			System.err.println("Failed to add directory '" + _directory.getName() + "'to FTP directory '" + m_sServerAddress + "': " + e);
			return false;
		}
	}

	@Override
	public boolean removeFile(File _file)
	{
		try
		{
			establishConnection();

			String convertedPath = Util.toUnixPath(_file.getPath());

			FTPFile serverFile = m_ftpClient.mlistFile(convertedPath);
			if (serverFile == null)
			{
				return false;
			}
			else if (serverFile.isDirectory())
			{
				for (FTPFile f : m_ftpClient.listFiles(convertedPath))
				{
					if (!(f.getName().equals(".") || f.getName().equals("..")))
					{
						removeFile(new File(convertedPath + "/" + f.getName()));
					}
				}
				return m_ftpClient.removeDirectory(convertedPath);
			}
			else
			{
				return m_ftpClient.deleteFile(convertedPath);
			}
		}
		catch (IOException e)
		{
			System.err.println("Failed to remove file '" + _file.getName() + "'to FTP directory '" + m_sServerAddress + "': " + e);
			return false;
		}
	}

	@Override
	public InputStream getFileStream(File _file)
	{
		try
		{
			establishConnection();

			// This is here to check if the requested is file holds text data
			// and not binary data, changing the file type as appropriate.
			// Unfortunately, since there is no method for checking the encoding
			// of a file in FTPFile, we have to just make an educated guess by
			// checking the extension of the file.
			if (Util.isExtension(m_listTextExtensions, Util.getExtension(_file)))
			{
				m_ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
			}

			InputStream input = m_ftpClient.retrieveFileStream(Util.toUnixPath(_file.getPath()));
			m_ftpClient.completePendingCommand();

			// Reads the stream in full, storing it another stream so the entire
			// file is stored in local memory
			ByteArrayOutputStream temp = new ByteArrayOutputStream();
			int b = input.read();
			while (b != -1)
			{
				temp.write(b);
				b = input.read();
			}

			InputStream toReturn = new ByteArrayInputStream(temp.toByteArray());

			return toReturn;
		}
		catch (IOException e)
		{
			System.err.println("Failed to read file '" + _file.getName() + "'from FTP directory '" + m_sServerAddress + "': " + e);
			return null;
		}
		finally
		{
			try
			{
				m_ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			}
			catch (IOException e)
			{
				System.err.println(e);
			}
		}
	}

	@Override
	public FileNode getDirectoryTree()
	{
		establishConnection();

		FileNode root = new FileNode("root", true, 0);
		try
		{
			for (FTPFile f : m_ftpClient.listFiles())
			{
				FileNode child = createNode("", f);
				if (child != null)
				{
					root.addChild(child);
				}
			}
		}
		catch (IOException e)
		{
			System.err.println("Could not retrieve snapshot for FTP directory '" + m_sServerAddress + "': " + e);
		}

		return root;
	}

	private FileNode createNode(String _sCurrentPath, FTPFile _file) throws IOException
	{
		// If a file is a symbolic link, just FORGET ABOUT IT! Do not add it to
		// the directory's directory tree since it's not a real file/directory
		if (_file.isSymbolicLink())
		{
			return null;
		}

		String filename = _file.getName();
		if (filename.equals(".") || filename.equals(".."))
			return null;

		// NOTE: Only add / if something is before it
		String fullPath = _sCurrentPath + ((_sCurrentPath.isEmpty()) ? "" : "/") + _file.getName();
		FileNode node = new FileNode(fullPath, _file.isDirectory(), _file.getTimestamp().getTimeInMillis());

		if (_file.isDirectory())
		{
			for (FTPFile f : m_ftpClient.listFiles(fullPath))
			{
				FileNode child = createNode(fullPath, f);
				if (child != null)
				{
					node.addChild(child);
				}
			}
		}

		return node;
	}

	@Override
	public boolean enabled()
	{
		return m_bEnabled;
	}

	private class NoOpTask extends TimerTask
	{
		@Override
		public void run()
		{
			try
			{
				m_ftpClient.sendNoOp();
			}
			catch (IOException e)
			{
				System.err.println("Could not send NOOP message to server: " + e);
			}
		}
	}

}
