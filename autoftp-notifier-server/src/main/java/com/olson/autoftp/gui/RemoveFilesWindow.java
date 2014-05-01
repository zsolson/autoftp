package com.olson.autoftp.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.olson.autoftp.FileNode;
import com.olson.autoftp.VirtualDirectory;

public class RemoveFilesWindow extends JFrame
{
	private VirtualDirectory m_dir1;
	private VirtualDirectory m_dir2;

	private JTree m_fileTree;
	private JScrollPane m_treeView;
	private JButton m_removeButton;
	private JButton m_closeButton;

	public RemoveFilesWindow(VirtualDirectory _dir1, VirtualDirectory dir2)
	{
		super("AutoFTP - RemoveFiles");
		m_dir1 = _dir1;
		m_dir2 = dir2;

		setVisible(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/resources/icons/tray_icon.png"));
		setLayout(new GridBagLayout());

		// Gets directory tree from first directory to get a list of files,
		// creating the appropriate list control with it
		DefaultMutableTreeNode treeRoot = convertToTreeNode(_dir1.getDirectoryTree());
		m_fileTree = new JTree(treeRoot);
		m_treeView = new JScrollPane(m_fileTree);
		m_treeView.setMinimumSize(new Dimension(640, 360));
		m_treeView.setPreferredSize(new Dimension(640, 360));
		m_treeView.setMaximumSize(new Dimension(640, 360));
		GridBagConstraints treeViewConstraints = new GridBagConstraints();
		treeViewConstraints.gridx = 0;
		treeViewConstraints.gridy = 0;
		treeViewConstraints.gridwidth = 8;
		treeViewConstraints.gridheight = 8;

		JPanel buttons = new JPanel();
		m_removeButton = new JButton("Remove");
		m_removeButton.addActionListener(new RemoveButtonListener());
		m_removeButton.setToolTipText("Highlight all the files you want to delete from both the local directory and server, then click this button.");
		m_closeButton = new JButton("Close");
		m_closeButton.addActionListener(new CloseButtonListener());
		buttons.add(m_removeButton);
		buttons.add(m_closeButton);
		buttons.setMaximumSize(new Dimension(640, 65));
		GridBagConstraints buttonsConstraints = new GridBagConstraints();
		buttonsConstraints.gridx = 4;
		buttonsConstraints.gridy = 8;
		buttonsConstraints.gridwidth = 4;
		buttonsConstraints.gridheight = 2;

		// Adds components to frame
		add(m_treeView, treeViewConstraints);
		add(buttons, buttonsConstraints);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
		setSize(640, 425);
		setResizable(false);
		// Centers the window
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
	}

	public void showWindow()
	{
		refreshTree();
		setVisible(true);
	}

	public void refreshTree()
	{
		DefaultMutableTreeNode root = convertToTreeNode(m_dir1.getDirectoryTree());
		DefaultTreeModel model = new DefaultTreeModel(root);
		m_fileTree.setModel(model);
		m_fileTree.repaint();
	}

	private DefaultMutableTreeNode convertToTreeNode(FileNode node)
	{
		// Gets rid of full path and just uses file/directory name
		String name = node.getPath();
		int index = name.lastIndexOf('/');
		if (index >= 0)
		{
			name = name.substring(name.lastIndexOf('/') + 1);
		}

		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(name);
		for (FileNode child : node.getChildren())
		{
			treeNode.add(convertToTreeNode(child));
		}
		return treeNode;
	}

	private class RemoveButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			TreePath[] paths = m_fileTree.getSelectionPaths();
			// Removes all the selected paths from BOTH directories, so the file
			// won't just be copied from the directory that still has it if we
			// only deleted it from ONE directory.
			if (paths != null)
			{
				for (TreePath treePath : paths)
				{
					File file = new File(treePath.getLastPathComponent().toString());
					m_dir1.removeFile(file);
					m_dir2.removeFile(file);
				}
			}
			refreshTree();
		}
	}

	private class CloseButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent _event)
		{
			setVisible(false);
		}
	}

}
