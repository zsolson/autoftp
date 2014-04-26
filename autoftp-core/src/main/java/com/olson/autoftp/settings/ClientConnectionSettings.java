package com.olson.autoftp.settings;

import com.olson.autoftp.Util;

public class ClientConnectionSettings
{
	private String m_sLocalDirectoryPath;
	private String m_sFTPDirectoryAddress;
	private String m_sServerUsername;
	private String m_sServerPassword;
	private String m_sNotifierServerAddress;
	private String m_sNotifierLocalPullPort;
	private String m_sNotifierServerPushPort;
	
	
	public ClientConnectionSettings()
	{
	}
	
	public ClientConnectionSettings(String _sLocalDirectoryPath, String _sFTPDirectoryAddress,
			String _sServerUsername, String _sServerPassword, String _sNotifierServerAddress,
			String _sNotifierLocalPullPort, String _sNotifierServerPushPort)
	{
		m_sLocalDirectoryPath = _sLocalDirectoryPath;
		m_sFTPDirectoryAddress = _sFTPDirectoryAddress;
		m_sServerUsername =_sServerUsername;
		m_sServerPassword =_sServerPassword;
		m_sNotifierServerAddress = _sNotifierServerAddress;
		m_sNotifierLocalPullPort = _sNotifierLocalPullPort;
		m_sNotifierServerPushPort = _sNotifierServerPushPort;
	}
	
	public String getLocalDirectoryPath()
	{
		return m_sLocalDirectoryPath;
	}
	
	public void setLocalDirectoryPath(String _sLocalDirectoryPath)
	{
		m_sLocalDirectoryPath = _sLocalDirectoryPath;
	}
	
	public String getFTPDirectoryAddress()
	{
		return m_sFTPDirectoryAddress;
	}
	
	public void setFTPDirectoryAddress(String _sFTPDirectoryAddress)
	{
		m_sFTPDirectoryAddress = _sFTPDirectoryAddress;
	}
	
	public String getServerUsername()
	{
		return m_sServerUsername;
	}
	
	public void setServerUsername(String _sServerUsername)
	{
		m_sServerUsername = _sServerUsername;
	}
	
	public String getServerPassword()
	{
		return m_sServerPassword;
	}
	
	public void setServerPassword(String _sServerPassword)
	{
		m_sServerPassword = _sServerPassword;
	}
	
	public String getNotifierServerAddress()
	{
		return m_sNotifierServerAddress;
	}
	
	public void setNotifierServerAddress(String _sNotifierServerAddress)
	{
		m_sNotifierServerAddress = _sNotifierServerAddress;
	}
	
	public String getNotifierLocalPullPort()
	{
		return m_sNotifierLocalPullPort;
	}
	
	public void setNotifierLocalPullPort(String _sNotifierLocalPullPort)
	{
		m_sNotifierLocalPullPort = _sNotifierLocalPullPort;
	}
	
	public String getNotifierServerPushPort()
	{
		return m_sNotifierServerPushPort;
	}
	
	public void setNotifierServerPushPort(String _sNotifierServerPushPort)
	{
		m_sNotifierServerPushPort = _sNotifierServerPushPort;
	}
	
	@Override
	public boolean equals(Object _obj) 
	{
		if (!(_obj instanceof ClientConnectionSettings))
			return false;
		
		ClientConnectionSettings otherSettings = (ClientConnectionSettings) _obj;
		
		boolean bIsEqual = Util.isStringsEqual(getLocalDirectoryPath(), otherSettings.getLocalDirectoryPath());
		if (!bIsEqual)
			return bIsEqual;
		bIsEqual = Util.isStringsEqual(getFTPDirectoryAddress(), otherSettings.getFTPDirectoryAddress());
		if (!bIsEqual)
			return bIsEqual;
		bIsEqual = Util.isStringsEqual(getServerUsername(), otherSettings.getServerUsername());
		if (!bIsEqual)
			return bIsEqual;
		bIsEqual = Util.isStringsEqual(getServerPassword(), otherSettings.getServerPassword());
		if (!bIsEqual)
			return bIsEqual;
		bIsEqual = Util.isStringsEqual(getNotifierServerAddress(), otherSettings.getNotifierServerAddress());
		if (!bIsEqual)
			return bIsEqual;
		bIsEqual = Util.isStringsEqual(getNotifierLocalPullPort(), otherSettings.getNotifierLocalPullPort());
		if (!bIsEqual)
			return bIsEqual;
		return Util.isStringsEqual(getNotifierServerPushPort(), otherSettings.getNotifierServerPushPort());
	}
}
