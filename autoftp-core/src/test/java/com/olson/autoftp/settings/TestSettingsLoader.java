package com.olson.autoftp.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.rmi.dgc.VMID;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

public class TestSettingsLoader {

	@Test
	public void testClientSettingsLoader() 
	{
		File settingsFile = new File("client_settings.autoftp");
		settingsFile.delete();
		
		SettingsLoader settingsLoader = new SettingsLoader();
		ClientConnectionSettings connSettings = new ClientConnectionSettings(new VMID().toString(), new VMID().toString(), new VMID().toString(), new VMID().toString(), new VMID().toString(), new VMID().toString(), new VMID().toString());
		ClientConnectionSettings loadedConnSettings = settingsLoader.loadClientSettings();
		assertNull(loadedConnSettings);
		
		settingsLoader.save(connSettings);
		loadedConnSettings = settingsLoader.loadClientSettings();
		assertEquals(connSettings, loadedConnSettings);
		
		settingsFile = new File("client_settings.autoftp");
		assertTrue(settingsFile.delete());
		
		loadedConnSettings = settingsLoader.loadClientSettings();
		assertNull(loadedConnSettings);
	}
	
	@Test
	public void testServerSettingsLoader() 
	{
		File settingsFile = new File("server_settings.autoftp");
		settingsFile.delete();
		
		SettingsLoader settingsLoader = new SettingsLoader();
		ServerConnectionSettings connSettings = new ServerConnectionSettings(new VMID().toString(), new LinkedList<ClientLocation>(Arrays.asList(new ClientLocation(new VMID().toString(), new VMID().toString()),new ClientLocation(new VMID().toString(), new VMID().toString()))));
		ServerConnectionSettings loadedConnSettings = settingsLoader.loadServerSettings();
		assertNull(loadedConnSettings);
		
		settingsLoader.save(connSettings);
		loadedConnSettings = settingsLoader.loadServerSettings();
		assertEquals(connSettings, loadedConnSettings);
		
		settingsFile = new File("server_settings.autoftp");
		assertTrue(settingsFile.delete());
		
		loadedConnSettings = settingsLoader.loadServerSettings();
		assertNull(loadedConnSettings);
	}

}
