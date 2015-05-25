package clientFiles;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
	
	private static Socket socket;
	private boolean connected;
	private Inport inport;
	private final String name = "Fedorov";

	private class Inport extends Thread
	{
		private ObjectInputStream in;
		
		public void run()
		{
			try
			{
				in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e)
			{
				System.err.println("Could not get inputstream from " + toString());
			}
			
			System.out.println(socket + " has connected input");
			
			while(true)
			{
				try
				{
					Thread.sleep(200);
				} catch (InterruptedException e)
				{
					System.err.println(toString() + " has input interrupted.");
				}
			}
		}
	}
	
	public Client(Socket newSocket)
	{
		socket = newSocket;
		connected = true;
		inport = new Inport();
		inport.start();
	}
	
	public void purge()
	{
		try
		{
			connected = false;
			socket.close();
		} catch (IOException e)
		{
			System.out.println("Could not purge " + socket);
		}
	}
	
	public String toString()
	{
		return new String(socket.toString());
	}
	
	public static void main(String[] args) throws Exception
	{      
		Client client = new Client(new Socket("h.nixo.no", 1337));
        System.out.println("Press 1 to close gracefully, any other nuber otherwise");
        
	}
	
	
	
	public boolean isConnected()
	{
		return connected;
	}

}
