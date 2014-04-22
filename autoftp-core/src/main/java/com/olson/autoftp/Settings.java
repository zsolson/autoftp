package com.olson.autoftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Settings {
    
    public Settings() {
        setToDefaults();
    }
    
    public boolean load(String filename) {
        try {
            FileInputStream file = new FileInputStream(filename);
            DataInputStream input = new DataInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
            localDirectoryPath = reader.readLine();
            ftpDirectoryAddress = reader.readLine();
            serverUsername = reader.readLine();
            serverPassword = reader.readLine();
            timeBetweenMerges = Long.decode(reader.readLine());
   
            input.close();
            
            return true;
        } catch (IOException e) {
            setToDefaults();
            System.err.println("Could not load AutoFTP settings: " + e);
            return false;
            
        } catch (NullPointerException e) {
            setToDefaults();
            System.err.println("Could not load AutoFTP settings: " + e);
            return false;
        }
    }
    
    private void setToDefaults() {
        localDirectoryPath = ftpDirectoryAddress
            = serverUsername = serverPassword = "";
        timeBetweenMerges = 30000; // 30 seconds is default
    }
    
    public boolean save(String filename) {
        BufferedWriter writer = null;
        
        try {
            writer = new BufferedWriter(new FileWriter(filename));
            
            writer.write(localDirectoryPath);
            writer.newLine();
            writer.write(ftpDirectoryAddress);
            writer.newLine();
            writer.write(serverUsername);
            writer.newLine();
            writer.write(serverPassword);
            writer.newLine();
            writer.write(String.valueOf(timeBetweenMerges));
            
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
    
    @Override
    public String toString() {
        return "Local Directory Path : " + localDirectoryPath +
            "\nFTP Directory Address: " + ftpDirectoryAddress +
            "\nServer Username: " + serverUsername +
            "\nServer Password: " + serverPassword +
            "\nTime Between Merges: " + timeBetweenMerges;
    }
    
    // Accessors
    public String getLocalDirectoryPath() {
        return localDirectoryPath;
    }
    public String getFTPDirectoryAddress() {
        return ftpDirectoryAddress;
    }
    public String getServerUsername() {
        return serverUsername;
    }
    public String getServerPassword() {
        return serverPassword;
    }
    public long getTimeBetweenMerges() {
        return timeBetweenMerges;
    }
    
    // Mutators
    public void setLocalDirectoryPath(String newPath) {
        localDirectoryPath = newPath;
    }
    public void setFTPDirectoryAddress(String newAddress) {
        ftpDirectoryAddress = newAddress;
    }
    public void setServerUsername(String newUsername) {
        serverUsername = newUsername;
    }
    public void setServerPassword(String newPassword) {
        serverPassword = newPassword;
    }
    public void setTimeBetweenMerges(long newTime) {
        timeBetweenMerges = newTime;
    }
    
    private String localDirectoryPath;
    private String ftpDirectoryAddress;
    private String serverUsername;
    private String serverPassword;
    private long timeBetweenMerges;
    
}
