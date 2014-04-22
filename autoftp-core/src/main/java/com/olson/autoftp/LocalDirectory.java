package com.olson.autoftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LocalDirectory implements VirtualDirectory
{
	private String m_sRootDirectory;
	private boolean m_bEnabled;

	public LocalDirectory(String _sDirectory)
	{
		setDirectory(_sDirectory);
	}

	@Override
	public boolean addFile(File _file, InputStream _contentStream, long _lLastModifiedTime)
	{
		try
		{
			File actualFile = new File(m_sRootDirectory + _file.getPath());

			// Check if the parent file (which is actually a directory) exists.
			// If it doesn't, then create it using addDirectory().
			File parent = new File(actualFile.getParent());
			if (!parent.exists())
			{
				addParentDirectory(parent);
			}

			OutputStream output = new FileOutputStream(actualFile);
			int b = _contentStream.read();
			while (b != -1)
			{
				output.write(b);
				b = _contentStream.read();
			}
			output.close();

			// After adding the file, set the last modified time to whatever was
			// given
			actualFile.setLastModified(_lLastModifiedTime);

			return true;
		}
		catch (IOException e)
		{
			System.err.println("Failed to add file '" + _file.getName() + "'to local directory '" + m_sRootDirectory + "': " + e);
			return false;
		}
	}

	public void setDirectory(String _sNewDirectory)
	{
		m_sRootDirectory = _sNewDirectory;
		m_bEnabled = (new File(m_sRootDirectory).exists());
	}

	/* Used by addFile to add a file's parent directory(s) if they don't exist. */
	private boolean addParentDirectory(File _directory)
	{
		return _directory.mkdirs();
	}

	@Override
	public boolean addDirectory(File _directory)
	{
		File actualFile = new File(m_sRootDirectory + _directory.getPath());
		return actualFile.mkdirs();
	}

	@Override
	public boolean removeFile(File _file)
	{
		File actualFile = new File(m_sRootDirectory + _file.getPath());
		if (actualFile.isDirectory())
		{
			for (File f : actualFile.listFiles())
			{
				removeNestedFile(f);
			}
		}
		return actualFile.delete();
	}

	private void removeNestedFile(File _file)
	{
		if (_file.isDirectory())
		{
			for (File f : _file.listFiles())
			{
				removeNestedFile(f);
			}
		}
		_file.delete();
	}

	@Override
	public InputStream getFileStream(File _file)
	{
		try
		{
			File actualFile = new File(m_sRootDirectory + _file.getPath());

			if (actualFile.isDirectory())
			{
				return null;
			}
			else
			{
				InputStream input = new FileInputStream(actualFile);
				return input;
			}
		}
		catch (IOException e)
		{
			System.err.println("Failed to read file '" + _file.getName() + "'from local directory '" + m_sRootDirectory + "': " + e);
			return null;
		}
	}

	@Override
	public FileNode getDirectoryTree()
	{
		File rootFile = new File(m_sRootDirectory);
		FileNode rootNode = new FileNode("root", true, 0);

		// Ensures NullPointer error doesn't occur below
		if (!rootFile.exists())
			return rootNode;

		for (File f : rootFile.listFiles())
		{
			rootNode.addChild(createNode(f));
		}

		return rootNode;
	}

	private FileNode createNode(File _file)
	{
		// Makes the file's path relative to the root directory
		String path = _file.getPath();
		path = path.substring(m_sRootDirectory.length());
		// Finally, makes sure that the path stored has been converted to
		// an UNIX path before being stored in the node
		path = Util.toUnixPath(path);

		FileNode node = new FileNode(path, _file.isDirectory(), _file.lastModified());
		if (_file.isDirectory())
		{
			for (File f : _file.listFiles())
			{
				node.addChild(createNode(f));
			}
		}
		return node;
	}

	@Override
	public boolean enabled()
	{
		return m_bEnabled;
	}

}
