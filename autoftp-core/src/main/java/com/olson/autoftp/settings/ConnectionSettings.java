package com.olson.autoftp.settings;

import com.olson.autoftp.Util;

public class ConnectionSettings
{
	private String m_sLocalDirectoryPath;
	private String m_sFTPDirectoryAddress;
	private String m_sServerUsername;
	private String m_sServerPassword;
	private String m_sNotifierServerAddress;
	private String m_sNotifierLocalPullPort;
	private String m_sNotifierServerPushPort;
	
	/**
	 * Do not use. Only for Kryo
	 */
	public ConnectionSettings()
	{
	}
	
	public ConnectionSettings(String _sLocalDirectoryPath, String _sFTPDirectoryAddress,
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
	
	public String getNotifierServerAddress()
	{
		return m_sNotifierServerAddress;
	}
	
	public String getNotifierLocalPullPort()
	{
		return m_sNotifierLocalPullPort;
	}
	
	public String getNotifierServerPushPort()
	{
		return m_sNotifierServerPushPort;
	}
	
	@Override
	public boolean equals(Object _obj) 
	{
		if (!(_obj instanceof ConnectionSettings))
			return false;
		
		ConnectionSettings otherSettings = (ConnectionSettings) _obj;
		
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
