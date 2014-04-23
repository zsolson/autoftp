package com.olson.autoftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import sun.misc.IOUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SettingsLoader
{
	private static Kryo m_serializer;
	
	private UserSettings m_userSettings;

	public SettingsLoader()
	{
		m_serializer = new Kryo();
		m_serializer.setClassLoader(AutoFTP.class.getClassLoader());
	}

	public boolean load(String m_sFilename)
	{
		try
		{
			Input input = new Input(new FileInputStream(m_sFilename));
			UserSettings userSettings = m_serializer.readObject(input, UserSettings.class);
			input.close();

			return true;
		}
		catch (IOException e)
		{
			System.err.println("Could not load AutoFTP settings: " + e);
			return false;

		}
		catch (NullPointerException e)
		{
			System.err.println("Could not load AutoFTP settings: " + e);
			return false;
		}
	}

	public boolean save(String _sFilename, UserSettings _settings)
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
