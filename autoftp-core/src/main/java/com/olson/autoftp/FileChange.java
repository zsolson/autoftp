package com.olson.autoftp;

public class FileChange
{
	private FileNode m_node;
	private Type m_type;
	private Target m_target;

	public enum Type
	{
		FILE_CREATE, 
		DIRECTORY_CREATE, 
		FILE_UPDATE
	}

	public enum Target
	{
		FROM_CLIENT_TO_SERVER, 
		FROM_SERVER_TO_CLIENT
	}

	public FileChange(FileNode _fileNode, Type _type, Target _target)
	{
		m_node = _fileNode;
		m_type = _type;
		m_target = _target;
	}

	public FileNode getNode()
	{
		return m_node;
	}

	public Type getType()
	{
		return m_type;
	}

	public Target getTarget()
	{
		return m_target;
	}

	@Override
	public String toString()
	{
		return "Path: " + m_node.getPath() + "\nType: " + m_type + "\nTarget: " + m_target;
	}

}
