package com.olson.autoftp.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.rmi.dgc.VMID;

import org.junit.Test;

public class TestSettingsLoader {

	@Test
	public void testClientSettingsLoader() 
	{
		SettingsLoader settingsLoader = new SettingsLoader();
		ClientConnectionSettings connSettings = new ClientConnectionSettings(new VMID().toString(), new VMID().toString(), new VMID().toString(), new VMID().toString(), new VMID().toString(), new VMID().toString(), new VMID().toString());
		ClientConnectionSettings loadedConnSettings = settingsLoader.load("test.conf");
		assertNull(loadedConnSettings);
		
		settingsLoader.save("test.conf", connSettings);
		loadedConnSettings = settingsLoader.load("test.conf");
		assertEquals(connSettings, loadedConnSettings);
		
		File settingsFile = new File("test.conf");
		assertTrue(settingsFile.delete());
		
		loadedConnSettings = settingsLoader.load("test.conf");
		assertNull(loadedConnSettings);
	}

}
