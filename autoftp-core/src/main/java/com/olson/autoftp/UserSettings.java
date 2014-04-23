package com.olson.autoftp;

public class UserSettings
{
	private String m_sLocalDirectoryPath;
	private String m_sFTPDirectoryAddress;
	private String m_sServerUsername;
	private String m_sServerPassword;
	private long m_lTimeBetweenMerges;
	
	public UserSettings(String _sLocalDirectoryPath, String _sFTPDirectoryAddress,
			String _sServerUsername, String _sServerPassword, long _lTimeBetweenMerges)
	{
		m_sLocalDirectoryPath = _sLocalDirectoryPath;
		m_sFTPDirectoryAddress = _sFTPDirectoryAddress;
		m_sServerUsername =_sServerUsername;
		m_sServerPassword =_sServerPassword;
		m_lTimeBetweenMerges = _lTimeBetweenMerges;
	}
	
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
	
	@Override
	public String toString()
	{
		return "Local Directory Path : " + m_sLocalDirectoryPath + "\nFTP Directory Address: " + m_sFTPDirectoryAddress + "\nServer Username: "
				+ m_sServerUsername + "\nServer Password: " + m_sServerPassword + "\nTime Between Merges: " + m_lTimeBetweenMerges;
	}
}
