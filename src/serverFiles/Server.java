package serverFiles;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Server extends Thread implements initListener
{

	private int status;
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private InetAddress hostAdress;
	private ArrayList<clientFiles.Client> users = new ArrayList<clientFiles.Client>();
	
	public void init()
	{
		try
		{
			hostAdress = InetAddress.getLocalHost();
		} catch (UnknownHostException e)
		{
			System.err.print("Could not get hostadress");
			System.exit(1);
		}
		try
		{
			ServerSocket serverSocket = new ServerSocket(666,0,hostAdress);
		} catch (IOException e)
		{
			 System.err.print("Could not open server on port 666");
			 System.exit(1);
		}
		System.out.println("Hostname: " + hostAdress + " on port: 666");
	}
	
	public void run()
	{
		System.out.println("Starting session.");

		while(users.size() > 1)
		{
			for(int i = 0; i < users.size(); i++)
			{
				if(!users.get(i).isConnected())
				{
					System.out.println(users.get(i) + " is no longer connected it seems.");
					users.remove(users.get(i));
				}
			}
			try
			{
				socket = serverSocket.accept();
			}catch (IOException e)
			{
				System.err.println("Could not get client.");
				e.printStackTrace();
			}
			System.out.println("Client " + socket + " has connected.");
			users.add(new clientFiles.Client(socket));
			
			try
			{
				Thread.sleep(200);
			} catch(InterruptedException e)
			{
				System.err.println("Room has been interrupted");
				e.printStackTrace();
			}
		}
	}
	
	public boolean isEmpty()
	{
		return users.size() < 0;
	}
	
/*	public void goOnline()
	{
		 try {
             System.out.println("waiting connetion");
             ServerSocket serverSocket = new ServerSocket(8999);
             while (true) {
                 Socket clientSocket = serverSocket.accept();
                 System.out.println("Connected");
                 bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 while((inputLine = bufferedReader.readLine()) != null)
                	 System.out.println(inputLine);
                 //Reader reader = new InputStreamReader(
                         //clientSocket.getInputStream());
                // reader.read(); // wait for input
                 
                 System.out.println("No exception");
             }
         } catch (IOException ex) {
             System.out.println("Exception");
             ex.printStackTrace();
         }
	}*/

	@Override
	public void isInit(String varInit, int state)
	{
		String stateListener = "Initiating";
		if(state == 1)
			stateListener = "Initiated";
		else if(state == 2)
			stateListener = "Stopped";
		System.out.println(varInit + ". Status: " + stateListener);
	}

}
