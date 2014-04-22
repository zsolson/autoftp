package com.olson.autoftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Settings
{
	private String m_sLocalDirectoryPath;
	private String m_sFTPDirectoryAddress;
	private String m_sServerUsername;
	private String m_sServerPassword;
	private long m_lTimeBetweenMerges;

	public Settings()
	{
		setToDefaults();
	}

	public boolean load(String m_sFilename)
	{
		try
		{
			FileInputStream file = new FileInputStream(m_sFilename);
			DataInputStream input = new DataInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			m_sLocalDirectoryPath = reader.readLine();
			m_sFTPDirectoryAddress = reader.readLine();
			m_sServerUsername = reader.readLine();
			m_sServerPassword = reader.readLine();
			m_lTimeBetweenMerges = Long.decode(reader.readLine());

			input.close();

			return true;
		}
		catch (IOException e)
		{
			setToDefaults();
			System.err.println("Could not load AutoFTP settings: " + e);
			return false;

		}
		catch (NullPointerException e)
		{
			setToDefaults();
			System.err.println("Could not load AutoFTP settings: " + e);
			return false;
		}
	}

	private void setToDefaults()
	{
		m_sLocalDirectoryPath = m_sFTPDirectoryAddress = m_sServerUsername = m_sServerPassword = "";
		m_lTimeBetweenMerges = 30000; // 30 seconds is default
	}

	public boolean save(String _sFilename)
	{
		BufferedWriter writer = null;

		try
		{
			writer = new BufferedWriter(new FileWriter(_sFilename));

			writer.write(m_sLocalDirectoryPath);
			writer.newLine();
			writer.write(m_sFTPDirectoryAddress);
			writer.newLine();
			writer.write(m_sServerUsername);
			writer.newLine();
			writer.write(m_sServerPassword);
			writer.newLine();
			writer.write(String.valueOf(m_lTimeBetweenMerges));

			return true;
		}
		catch (IOException e)
		{
			return false;
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.flush();
					writer.close();
				}
			}
			catch (IOException e)
			{
				System.err.println(e);
			}
		}
	}

	@Override
	public String toString()
	{
		return "Local Directory Path : " + m_sLocalDirectoryPath + "\nFTP Directory Address: " + m_sFTPDirectoryAddress + "\nServer Username: "
				+ m_sServerUsername + "\nServer Password: " + m_sServerPassword + "\nTime Between Merges: " + m_lTimeBetweenMerges;
	}

	// Accessors
	public String getLocalDirectoryPath()
	{
		return m_sLocalDirectoryPath;
	}

	public String getFTPDirectoryAddress()
	{
		return m_sFTPDirectoryAddress;
	}

	public String getServerUsername()
	{
		return m_sServerUsername;
	}

	public String getServerPassword()
	{
		return m_sServerPassword;
	}

	public long getTimeBetweenMerges()
	{
		return m_lTimeBetweenMerges;
	}

	// Mutators
	public void setLocalDirectoryPath(String _sNewPath)
	{
		m_sLocalDirectoryPath = _sNewPath;
	}

	public void setFTPDirectoryAddress(String _sNewAddress)
	{
		m_sFTPDirectoryAddress = _sNewAddress;
	}

	public void setServerUsername(String _sNewUsername)
	{
		m_sServerUsername = _sNewUsername;
	}

	public void setServerPassword(String _sNewPassword)
	{
		m_sServerPassword = _sNewPassword;
	}

	public void setTimeBetweenMerges(long _lNewTime)
	{
		m_lTimeBetweenMerges = _lNewTime;
	}

}
