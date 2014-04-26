package com.olson.autoftp;

import java.io.File;
import java.io.InputStream;

public interface VirtualDirectory
{

	boolean addFile(File _file, InputStream _contentStream, long _lLastModifiedTime);

	boolean addDirectory(File _directory);

	boolean removeFile(File _file);

	InputStream getFileStream(File _file);

	FileNode getDirectoryTree();

	boolean exists();

}