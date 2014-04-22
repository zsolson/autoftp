package com.olson.autoftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

public class Util
{

	public static String toUnixPath(String _sPath)
	{
		return _sPath.replace("\\", "/");
	}

	public static String getExtension(File _file)
	{
		String name = _file.getName();
		int pos = name.lastIndexOf('.');
		return name.substring(pos + 1);
	}

	public static boolean isExtension(List<String> _listExtensions, String _sExtension)
	{
		return _listExtensions.contains(_sExtension);
	}

	public static String readFileAsString(String _sPath)
	{
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(new File(_sPath));

			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		}
		catch (IOException e)
		{
			System.err.println(e);
			return "";
		}
		finally
		{
			try
			{
				if (stream != null)
					stream.close();
			}
			catch (IOException e)
			{
				System.err.println(e);
				return "";
			}
		}
	}

}
