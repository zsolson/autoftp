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

public class RemoveFilesWindow extends JFrame {
    
    public RemoveFilesWindow(VirtualDirectory dir1, VirtualDirectory dir2) {
        super("AutoFTP - RemoveFiles");
        this.dir1 = dir1;
        this.dir2 = dir2;
        
        setVisible(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        setLayout(new GridBagLayout());
        
        // Gets directory tree from first directory to get a list of files,
        // creating the appropriate list control with it
        DefaultMutableTreeNode treeRoot = convertToTreeNode(dir1.getDirectoryTree());
        fileTree = new JTree(treeRoot);
        treeView = new JScrollPane(fileTree);
        treeView.setMinimumSize(new Dimension(640, 360));
        treeView.setPreferredSize(new Dimension(640, 360));
        treeView.setMaximumSize(new Dimension(640, 360));
        GridBagConstraints treeViewConstraints = new GridBagConstraints();
        treeViewConstraints.gridx = 0;
        treeViewConstraints.gridy = 0;
        treeViewConstraints.gridwidth = 8;
        treeViewConstraints.gridheight = 8;
                
        JPanel buttons = new JPanel();
        removeButton = new JButton("Remove");
        removeButton.addActionListener(new RemoveButtonListener());
        removeButton.setToolTipText("Highlight all the files you want to delete from both the local directory and server, then click this button.");
        closeButton = new JButton("Close");
        closeButton.addActionListener(new CloseButtonListener());
        buttons.add(removeButton);
        buttons.add(closeButton);
        buttons.setMaximumSize(new Dimension(640, 65));
        GridBagConstraints buttonsConstraints = new GridBagConstraints();
        buttonsConstraints.gridx = 4;
        buttonsConstraints.gridy = 8;
        buttonsConstraints.gridwidth = 4;
        buttonsConstraints.gridheight = 2;
        
        // Adds components to frame
        add(treeView, treeViewConstraints);
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
    
    public void showWindow() {
        refreshTree();
        setVisible(true);
    }
    
    public void refreshTree() {
        DefaultMutableTreeNode root = convertToTreeNode(dir1.getDirectoryTree());
        DefaultTreeModel model = new DefaultTreeModel(root);
        fileTree.setModel(model);
        fileTree.repaint();
    }
    
    private DefaultMutableTreeNode convertToTreeNode(FileNode node) {
        // Gets rid of full path and just uses file/directory name
        String name = node.getPath();
        int index = name.lastIndexOf('/');
        if (index >= 0) {
            name = name.substring(name.lastIndexOf('/') + 1);
        }
        
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(name);
        for (FileNode child : node.getChildren()) {
            treeNode.add(convertToTreeNode(child));
        }
        return treeNode;
    }
    
    private VirtualDirectory dir1;
    private VirtualDirectory dir2;
    
    private JTree fileTree;
    private JScrollPane treeView;
    private JButton removeButton;
    private JButton closeButton;
    
    private class RemoveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            TreePath[] paths = fileTree.getSelectionPaths();
            // Removes all the selected paths from BOTH directories, so the file
            // won't just be copied from the directory that still has it if we
            // only deleted it from ONE directory.
            if (paths != null) {
                for (TreePath treePath : paths) { 
                    File file = new File(treePath.getLastPathComponent().toString());
                    dir1.removeFile(file);
                    dir2.removeFile(file);
                }
            }
            refreshTree();
        }
    }
    
    private class CloseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    }
    
}
