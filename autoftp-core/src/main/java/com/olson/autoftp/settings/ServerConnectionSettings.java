package com.olson.autoftp.settings;

import java.util.Collection;

import com.olson.autoftp.Util;

public class ServerConnectionSettings
{
	private String m_sNotifierServerPushPort;
	private Collection<ClientLocation> m_collClientLocations;
	
	
	public ServerConnectionSettings()
	{
	}
	
	public ServerConnectionSettings(String _sNotifierServerPushPort, Collection<ClientLocation> _collClientLocations)
	{
		m_sNotifierServerPushPort = _sNotifierServerPushPort;
		m_collClientLocations = _collClientLocations;
	}
	
	public String getNotifierServerPushPort()
	{
		return m_sNotifierServerPushPort;
	}
	
	public void setNotifierServerPushPort(String _sNotifierServerPushPort)
	{
		m_sNotifierServerPushPort = _sNotifierServerPushPort;
	}
	
	public Collection<ClientLocation> getClientLocations()
	{
		return m_collClientLocations;
	}
	
	public void setClientLocations(Collection<ClientLocation> _collClientLocations)
	{
		m_collClientLocations = _collClientLocations;
	}
	
	@Override
	public boolean equals(Object _obj) 
	{
		if (!(_obj instanceof ServerConnectionSettings))
			return false;
		
		ServerConnectionSettings otherSettings = (ServerConnectionSettings) _obj;
		
		boolean bIsEqual = Util.isStringsEqual(getNotifierServerPushPort(), otherSettings.getNotifierServerPushPort());
		if (!bIsEqual)
			return bIsEqual;
		return Util.isEqual(getClientLocations(), otherSettings.getClientLocations());
	}
}
