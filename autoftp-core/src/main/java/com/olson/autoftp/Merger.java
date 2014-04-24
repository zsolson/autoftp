package com.olson.autoftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Merger
{
	private VirtualDirectory m_dir1;
	private VirtualDirectory m_dir2;
	private OldChangeDetector m_changeDetector;

	public Merger(VirtualDirectory _dir1, VirtualDirectory _dir2, OldChangeDetector _changeDetector)
	{
		m_dir1 = _dir1;
		m_dir2 = _dir2;
		m_changeDetector = _changeDetector;
	}

	public void merge()
	{
		ArrayList<FileChange> changes = m_changeDetector.findChanges(m_dir1, m_dir2);

		VirtualDirectory sender = null;
		VirtualDirectory target = null;
		for (FileChange change : changes)
		{
			System.out.println(change + "\n");
			FileNode node = change.getNode();

			if (change.getTarget() == FileChange.Target.FROM_CLIENT_TO_SERVER)
			{
				sender = m_dir1;
				target = m_dir2;
			}
			else
			{
				sender = m_dir2;
				target = m_dir1;
			}

			switch (change.getType())
			{
				case FILE_CREATE:
					// FILE_UPDATE is essentially creating the file again since
					// we
					// have to upload the entire file stream, so just use the
					// same code
				case FILE_UPDATE:
					InputStream input = sender.getFileStream(new File(node.getPath()));
					target.addFile(new File(node.getPath()), input, node.lastModified());
					try
					{
						input.close();
					}
					catch (IOException e)
					{
						System.err.println(e);
					}
				case DIRECTORY_CREATE:
					target.addDirectory(new File(node.getPath()));
					break;
			}
		}
	}

}
