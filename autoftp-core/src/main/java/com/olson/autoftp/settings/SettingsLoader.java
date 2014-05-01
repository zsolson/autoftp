package com.olson.autoftp.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SettingsLoader
{
	private static Kryo m_serializer;
	private static String CLIENT_SETTINGS_FILE_NAME = "client_settings.autoftp";
	private static String SERVER_SETTINGS_FILE_NAME = "server_settings.autoftp";
	
	private ClientConnectionSettings m_userSettings;

	public SettingsLoader()
	{
		m_serializer = new Kryo();
		m_serializer.setClassLoader(SettingsLoader.class.getClassLoader());
	}

	public ClientConnectionSettings loadClientSettings()
	{
		return load(ClientConnectionSettings.class, CLIENT_SETTINGS_FILE_NAME);
	}

	public ServerConnectionSettings loadServerSettings()
	{
		return load(ServerConnectionSettings.class, SERVER_SETTINGS_FILE_NAME);
	}
	
	private <T> T load(Class<T> _clazz, String _sFileName)
	{
		try
		{
			Input input = new Input(new FileInputStream(_sFileName));
			T settings = m_serializer.readObject(input, _clazz);
			input.close();

			return settings;
		}
		catch (IOException e)
		{
			System.err.println("Could not load AutoFTP settings: " + e);
		}
		catch (NullPointerException e)
		{
			System.err.println("Could not load AutoFTP settings: " + e);
		}
		return null;
	}

	public boolean save(ClientConnectionSettings _settings)
	{
		return save(_settings, CLIENT_SETTINGS_FILE_NAME);
	}
	
	public boolean save(ServerConnectionSettings _settings)
	{
		return save(_settings, SERVER_SETTINGS_FILE_NAME);
	}
	
	private <T> boolean save(T _settings, String _sFileName)
	{
		try
		{
			Output output = new Output(new FileOutputStream(new File(_sFileName)));
			m_serializer.writeObject(output, _settings);
			output.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return m_userSettings == null ? "" : m_userSettings.toString();
	}

	
}
