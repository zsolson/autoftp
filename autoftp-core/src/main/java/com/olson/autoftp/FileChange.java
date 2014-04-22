package com.olson.autoftp;

public class FileChange {

    public enum Type {
        FILE_CREATE,
        DIRECTORY_CREATE,
        FILE_UPDATE
    }
    
    public enum Target {
        FROM_CLIENT_TO_SERVER,
        FROM_SERVER_TO_CLIENT
    }
    
    public FileChange(FileNode fileNode, Type type, Target target) {
        this.node = fileNode;
        this.type = type;
        this.target = target;
    }
    
    public FileNode getNode() {
        return node;
    }
    public Type getType() { 
        return type;
    }
    public Target getTarget() {
        return target;
    }
    
    @Override
    public String toString() {
        return "Path: " + node.getPath() + "\nType: " + type + "\nTarget: " + target;
    }
    
    private FileNode node;
    private Type type;
    private Target target;
    
    
}
