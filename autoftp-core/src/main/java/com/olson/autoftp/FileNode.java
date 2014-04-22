package com.olson.autoftp;

import java.util.ArrayList;

public class FileNode {
    
    public FileNode(String path, boolean directory, long lastModifiedDate) {
        this.path = path;
        this.directory = directory;
        this.lastModifiedDate = lastModifiedDate;
        this.children = new ArrayList<FileNode>();
    }
    
    public void addChild(FileNode child) {
        children.add(child);
    }
    
    public boolean removeChild(FileNode child) {
        return children.remove(child);
    }
    
    /* Overriden so it is possible to search for a file in a list of snapshots
     * using contains method. */
    @Override 
    public boolean equals(Object o) {
        FileNode other = (FileNode)o;
        return (path.equals(other.getPath()));
    }
    
    @Override
    public String toString() { 
        String s = ((isDirectory()) ? "Directory" : "File") + "\nPath: " + path +
            "\nLast Modified: " + lastModifiedDate + "\n\n";
        for (FileNode node : children) {
            s += node.toString();
        }
        return s;
    }
  
    public String getPath() { return path; }
    public long lastModified() { return lastModifiedDate; }
    public boolean isDirectory() { return directory; }
    public boolean isLeaf() { return children.isEmpty(); }
    public ArrayList<FileNode> getChildren() { return children; }
    
    private String path;
    private boolean directory;
    private long lastModifiedDate;
    private ArrayList<FileNode> children;    
    
}
