package com.olson.autoftp.settings;

import java.util.Collection;

import com.olson.autoftp.Util;

public class ClientLocation
{
	private String m_sClientAddress;
	private String m_sPullPort;
	
	
	public ClientLocation()
	{
	}
	
	public ClientLocation(String _sClientAddress, String _sPullPort)
	{
		m_sClientAddress = _sClientAddress;
		m_sPullPort = _sPullPort;
	}
	
	public String getClientAddress()
	{
		return m_sClientAddress;
	}
	
	public void setClientAddress(String _sClientAddress)
	{
		m_sClientAddress = _sClientAddress;
	}
	
	public String getPullPort()
	{
		return m_sPullPort;
	}
	
	public void setPullPort(String _sPullPort)
	{
		m_sPullPort = _sPullPort;
	}
	
	@Override
	public boolean equals(Object _obj) 
	{
		if (!(_obj instanceof ClientLocation))
			return false;
		
		ClientLocation otherSettings = (ClientLocation) _obj;
		
		boolean bIsEqual = Util.isStringsEqual(getClientAddress(), otherSettings.getClientAddress());
		if (!bIsEqual)
			return bIsEqual;
		return Util.isStringsEqual(getPullPort(), otherSettings.getPullPort());
	}
}
