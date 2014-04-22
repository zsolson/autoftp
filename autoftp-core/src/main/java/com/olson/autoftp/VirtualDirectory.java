package com.olson.autoftp;

import java.io.File;
import java.io.InputStream;

public interface VirtualDirectory {
	
    boolean addFile(File file, InputStream content, long lastModifiedTime);
    boolean addDirectory(File directory);
    boolean removeFile(File file);
    
    InputStream getFileStream(File file);
    FileNode getDirectoryTree();
    
    boolean enabled();

}