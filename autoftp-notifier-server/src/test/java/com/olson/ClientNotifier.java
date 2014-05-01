package com.olson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.olson.autoftp.request.FilePullRequest;

public class ClientNotifier {

	private Kryo m_serializer;
	
	@Test
	public void test()
	{
		m_serializer = new Kryo();
		m_serializer.setClassLoader(this.getClass().getClassLoader());
		
		Socket socket;
		try {
			socket = new Socket((String)null, 25000);
			OutputStream socketSender = socket.getOutputStream();
			Output kryoOutput = new Output(socketSender);
			m_serializer.writeObject(kryoOutput, new FilePullRequest(new LinkedList<String>(Arrays.asList("New Text Document.txt"))));
			kryoOutput.flush();
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
