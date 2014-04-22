package com.olson.autoftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Merger {

    public Merger(VirtualDirectory dir1, VirtualDirectory dir2, ChangeDetector changeDetector) {
        this.dir1 = dir1;
        this.dir2 = dir2;
        this.changeDetector = changeDetector;
    }
    
    public void merge() {
        ArrayList<FileChange> changes = changeDetector.findChanges(dir1, dir2);
        
        VirtualDirectory sender = null;
        VirtualDirectory target = null;
        for (FileChange change : changes) {
            System.out.println(change + "\n");
            FileNode node = change.getNode();
            
            if (change.getTarget() == FileChange.Target.FROM_CLIENT_TO_SERVER) {
                sender = dir1;
                target = dir2;
            } else {
                sender = dir2;
                target = dir1;
            }
            
            switch (change.getType()) {
                case FILE_CREATE:
                // FILE_UPDATE is essentially creating the file again since we
                // have to upload the entire file stream, so just use the same code
                case FILE_UPDATE: 
                    InputStream input = sender.getFileStream(new File(node.getPath()));
                    target.addFile(new File(node.getPath()), input, node.lastModified());
                    try {
                        input.close();
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                case DIRECTORY_CREATE:
                    target.addDirectory(new File(node.getPath()));
                    break;
            }
        }        
    }
    
    private VirtualDirectory dir1;
    private VirtualDirectory dir2;
    private ChangeDetector changeDetector;
    
}
