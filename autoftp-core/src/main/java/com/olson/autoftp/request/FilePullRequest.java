package com.olson.autoftp.request;

import java.util.Collection;

public class FilePullRequest 
{
	private Collection<String> m_collFileNames;
	
	public FilePullRequest() {
		// TODO Auto-generated constructor stub
	}
	
	public FilePullRequest(Collection<String> _collFileNames)
	{
		m_collFileNames = _collFileNames;
	}
	
	public Collection<String> getFileNamesToPull()
	{
		return m_collFileNames;
	}
}
