package com.olson.autoftp;

import java.util.ArrayList;

public class FileNode
{
	private String m_sPath;
	private boolean m_directory;
	private long m_lLastModifiedDate;
	private ArrayList<FileNode> m_listChildren;

	public FileNode(String path, boolean _directory, long _lLastModifiedDate)
	{
		m_sPath = path;
		m_directory = _directory;
		m_lLastModifiedDate = _lLastModifiedDate;
		m_listChildren = new ArrayList<FileNode>();
	}

	public String getPath()
	{
		return m_sPath;
	}

	public long lastModified()
	{
		return m_lLastModifiedDate;
	}

	public boolean isDirectory()
	{
		return m_directory;
	}

	public boolean isLeaf()
	{
		return m_listChildren.isEmpty();
	}

	public ArrayList<FileNode> getChildren()
	{
		return m_listChildren;
	}

	public void addChild(FileNode _child)
	{
		m_listChildren.add(_child);
	}

	public boolean removeChild(FileNode _child)
	{
		return m_listChildren.remove(_child);
	}

	/*
	 * Overriden so it is possible to search for a file in a list of snapshots
	 * using contains method.
	 */
	@Override
	public boolean equals(Object _obj)
	{
		FileNode other = (FileNode) _obj;
		return (m_sPath.equals(other.getPath()));
	}

	@Override
	public String toString()
	{
		String s = ((isDirectory()) ? "Directory" : "File") + "\nPath: " + m_sPath + "\nLast Modified: " + m_lLastModifiedDate + "\n\n";
		for (FileNode node : m_listChildren)
		{
			s += node.toString();
		}
		return s;
	}

}
