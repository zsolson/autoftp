package com.olson.autoftp;

import java.util.ArrayList;

public class OldChangeDetector
{
	private ArrayList<FileChange> m_listChanges;

	public OldChangeDetector()
	{
		m_listChanges = new ArrayList<FileChange>();
	}

	public ArrayList<FileChange> findChanges(VirtualDirectory dir1, VirtualDirectory dir2)
	{
		FileNode dir1Root = dir1.getDirectoryTree();
		FileNode dir2Root = dir2.getDirectoryTree();

		getChanges(dir1Root.getChildren(), dir2Root.getChildren(), FileChange.Target.FROM_CLIENT_TO_SERVER);
		getChanges(dir2Root.getChildren(), dir1Root.getChildren(), FileChange.Target.FROM_SERVER_TO_CLIENT);

		ArrayList<FileChange> temp = (ArrayList<FileChange>) m_listChanges.clone();
		m_listChanges.clear();
		return temp;
	}

	private void getChanges(ArrayList<FileNode> _nodes1, ArrayList<FileNode> _nodes2, FileChange.Target _target)
	{
		// This only occurs FROM_SERVER_TO_CLIENT, not the other way around.
		// I imagine I'm doing something wrong when it comes to sending/storing
		// the last modified date of a file UPLOADED to the server, so it's
		// firing
		// false positives.
		// One potential issue that's causing this is that the FTP server stores
		// the last modified date slightly differently, rounding it off to the
		// nearest minute.

		// Check if anything is in FIRST set of nodes that isn't in the SECOND
		for (FileNode n1 : _nodes1)
		{
			int index = _nodes2.indexOf(n1);

			if (n1.isDirectory())
			{
				// If it was found in nodes2, then check the directory's
				// children for changes
				if (index != -1)
				{
					ArrayList<FileNode> n2Children = _nodes2.get(index).getChildren();
					getChanges(n1.getChildren(), n2Children, _target);
				}
				else
				{
					// If the directory is EMPTY, create it
					if (n1.isLeaf())
					{
						// DIR1 TO DIR2 - CREATE n1
						m_listChanges.add(new FileChange(n1, FileChange.Type.DIRECTORY_CREATE, _target));
						// Otherwise, make FILE_CREATE/DIRECTORY_CREATE events
						// for the directory's children
					}
					else
					{
						createDirectoryChanges(n1, _target);
					}
				}

			}
			else
			{

				if (index != -1)
				{
					// If node1's last modified date is NEWER (higher) than
					// node2's, update node1
					if (n1.lastModified() > _nodes2.get(index).lastModified())
					{
						// DIR1 TO DIR2 - UPDATE n1
						m_listChanges.add(new FileChange(n1, FileChange.Type.FILE_UPDATE, _target));
					}
				}
				else
				{
					// DIR1 TO DIR2 - CREATE n1
					m_listChanges.add(new FileChange(n1, FileChange.Type.FILE_CREATE, _target));
				}

			}
		}
	}

	private void createDirectoryChanges(FileNode _directory, FileChange.Target _target)
	{
		for (FileNode node : _directory.getChildren())
		{
			if (node.isDirectory())
			{
				if (node.isLeaf())
				{
					// DIR1 TO DIR2 - CREATE node
					m_listChanges.add(new FileChange(node, FileChange.Type.DIRECTORY_CREATE, _target));
				}
				else
				{
					createDirectoryChanges(node, _target);
				}
			}
			else
			{
				// DIR1 TO DIR2 - CREATE node
				m_listChanges.add(new FileChange(node, FileChange.Type.FILE_CREATE, _target));
			}
		}
	}

}
