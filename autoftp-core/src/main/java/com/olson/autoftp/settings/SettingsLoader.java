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
	
	private ClientConnectionSettings m_userSettings;

	public SettingsLoader()
	{
		m_serializer = new Kryo();
		m_serializer.setClassLoader(SettingsLoader.class.getClassLoader());
	}

	public ClientConnectionSettings load(String m_sFilename)
	{
		try
		{
			Input input = new Input(new FileInputStream(m_sFilename));
			ClientConnectionSettings userSettings = m_serializer.readObject(input, ClientConnectionSettings.class);
			input.close();

			return userSettings;
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

	public boolean save(String _sFilename, ClientConnectionSettings _settings)
	{
		try
		{
			Output output = new Output(new FileOutputStream(new File(_sFilename)));
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
