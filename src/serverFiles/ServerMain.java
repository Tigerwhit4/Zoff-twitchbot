package serverFiles;

import java.util.Scanner;

public class ServerMain
{
	private Server server;

	private void run()
	{
		this.server = new Server();
		server.init();
		server.run();
		//server.goOnline();
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		ServerMain main = new ServerMain();
		main.run();
	}

}
