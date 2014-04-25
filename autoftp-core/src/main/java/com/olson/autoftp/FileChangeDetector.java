package com.olson.autoftp;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

public class FileChangeDetector
{
	private String m_sRootDirectory = null;
	private boolean m_bEnabled = false;
	
	public boolean setRootDirectory(String _sDirectoryName) 
	{
		m_bEnabled = false;
		if (_sDirectoryName == null || _sDirectoryName.isEmpty())
			return false;
		
		m_sRootDirectory = _sDirectoryName;
		m_bEnabled = (new File(m_sRootDirectory).exists());
		return m_bEnabled;
	}
	
	public Collection<String> findChangesAfter(Calendar _calLastCheck)
	{
		Collection<File> collFoundFiles = FileUtils.listFiles(new File(m_sRootDirectory), new AgeFileFilter(_calLastCheck.getTime(), false), TrueFileFilter.TRUE);
		Collection<String> collFileNames = new LinkedList<String>();
		if (collFoundFiles != null)
		{
			for (File file : collFoundFiles)
			{
//				StringUtils.removeStart(str, remove)
				collFileNames.add(file.getPath());
			}
		}
		return collFileNames;
	}
}
