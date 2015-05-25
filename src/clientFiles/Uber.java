package clientFiles;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import TopKek.Crypto;

import com.socket.Message;

public class Uber
{
	private static String sender = "Fedorov";
	private Socket socket;
	public static Uber uber;
	private ObjectOutputStream Out;
	private ObjectInputStream In;
	public Crypto crypt;
	public ArrayList<String> onlineMembers = new ArrayList<String>();
	public final Runnable outHandler;
	public final Runnable generalRun;
	private Updater updater;
	
	public Uber(Socket socket, String pass, Updater updater) throws IOException, GeneralSecurityException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
	{
		this.updater = updater;
		
		System.setProperty("file.encoding","UTF-8");
		Field charset = Charset.class.getDeclaredField("defaultCharset");
    	charset.setAccessible(true);
    	charset.set(null,null);
		this.socket = socket;
		Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
        this.crypt = new Crypto(pass, "Shhhh!! MuCH NSA must BE SECTRETORNMONOERN KLASUI GDas7das7kt6RDas57rjD iuYTSYU&ARDU(AS% (IASG KslSHf dkya g,UGSdo6asfio76GIOsdt7 ASFkd");
        
        
        generalRun = new Runnable() {
            public void run() {
                try
				{
					Uber.this.run();
				} catch (BadPaddingException | InvalidParameterSpecException
						| InvalidKeySpecException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                // one can use a non-final typed variable
                // to store, which then<1>
            }
        };
        
        outHandler = new Runnable() {
            public void run() {
                Uber.this.outMsgHandler();
                // one can use a non-final typed variable
                // to store, which then<1>
            }
        };
	}
	
	public void run() throws BadPaddingException, InvalidParameterSpecException, InvalidKeySpecException
	{
		try
		{
			//send(new Message("test", "testUser", "testContent", "SERVER"));
			send(new Message("signup", sender, InetAddress.getLocalHost().getHostName(), "SERVER"));
			send(new Message("login", sender, InetAddress.getLocalHost().getHostName(), "SERVER"));
			send(new Message("message", sender, ".join", "SERVER"));
			
		} catch (UnknownHostException e1)
		{
			e1.printStackTrace();
		}
		while(true)
		{
			try
			{
				Message msg = (Message) In.readObject();
				//System.out.println("Outgoing : "+msg.toString());
			//	System.out.println("Incoming : "+msg.toString());
				String abc = msg + "";
				if(msg.type.equals("signin"))
				{
					System.out.println(msg.content + " joined.");
					onlineMembers.add(msg.content);
				}else if(msg.type.equals("signout"))
				{
					System.out.println(msg.content + " left.");
					onlineMembers.remove(msg.content);
				}else if(msg.type.equals("newuser"))
				{
					if(!msg.content.equals(sender))
						onlineMembers.add(msg.content);
				}
				try
				{
					String messageCont = crypt.decrypt(crypt.unKek(msg.content));
					//String stringMsg = msg + "";
					//stringMsg = crypt.encrypt(stringMsg);
					if(!messageCont.equals("** ENCRYPTED **") && !messageCont.equals("") && !messageCont.equals("000"))
						System.out.println(msg.sender + ": " + crypt.decrypt(crypt.unKek(msg.content)));
					 //send(new Message("message", sender, "NO", stringMsg));
				} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | InvalidKeyException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//	send(new Message("message", sender, msg, "All"));
				
			} catch (ClassNotFoundException | IOException e)
			{
				System.err.println("Connection Failure: \n");
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void send(Message msg)
	{
		try
		{
			Out.writeObject(msg);
			Out.flush();
		} catch(IOException e)
		{
			System.err.println("Exception Uber send()");
		}
	}
	
	public void outMsgHandler()
	{
		Scanner sc = new Scanner(System.in, "UTF8");
		while(sc.hasNextLine())
		{
			String msgToSend = sc.nextLine();
			if(!msgToSend.substring(0,1).equals("/"))
			{
				try
				{
					msgToSend = crypt.encrypt(msgToSend);
				} catch (InvalidKeyException | IllegalBlockSizeException
						| BadPaddingException | UnsupportedEncodingException
						| InvalidParameterSpecException | InvalidKeySpecException
						| NoSuchAlgorithmException | NoSuchPaddingException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msgToSend = crypt.kek(msgToSend);
				send(new Message("message", sender, msgToSend, "All"));
			}else
			{
				if(msgToSend.equals("/online"))
					System.out.println(onlineMembers + " : " + onlineMembers.size());
				else if(msgToSend.substring(0).equals("/fbi"))
				{
					String pastNick = sender;
					send(new Message("signout", sender, sender, "SERVER"));
					sender = "FBI-Agent";
					send(new Message("test", "testUser", "testContent", "SERVER"));
					try
					{
						send(new Message("signup", sender, InetAddress.getLocalHost().getHostName(), "SERVER"));
						send(new Message("login", sender, InetAddress.getLocalHost().getHostName(), "SERVER"));
					} catch (UnknownHostException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//send(new Message("message", sender, ".join", "SERVER"));
					try
					{
						msgToSend = crypt.encrypt("Partyvan Incoming Boys.");
					} catch (InvalidKeyException | IllegalBlockSizeException
							| BadPaddingException | UnsupportedEncodingException
							| InvalidParameterSpecException | InvalidKeySpecException
							| NoSuchAlgorithmException | NoSuchPaddingException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msgToSend = crypt.kek(msgToSend);
					send(new Message("message", sender, msgToSend, "All"));
					String newNick = pastNick;
					onlineMembers = new ArrayList<String>();
					onlineMembers.add(pastNick);
					send(new Message("signout", sender, sender, "SERVER"));
					sender = newNick;
					send(new Message("test", "testUser", "testContent", "SERVER"));
					try
					{
						send(new Message("signup", newNick, InetAddress.getLocalHost().getHostName(), "SERVER"));
						send(new Message("login", newNick, InetAddress.getLocalHost().getHostName(), "SERVER"));
					} catch (UnknownHostException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//send(new Message("message", newNick, ".join", "SERVER"));
				}else if(msgToSend.substring(0,6).equals("/nick "))
				{
					String newNick = msgToSend.substring(6);
					onlineMembers = new ArrayList<String>();
					send(new Message("signout", sender, sender, "SERVER"));
					sender = newNick;
					send(new Message("test", "testUser", "testContent", "SERVER"));
					try
					{
						send(new Message("signup", newNick, InetAddress.getLocalHost().getHostName(), "SERVER"));
						send(new Message("login", newNick, InetAddress.getLocalHost().getHostName(), "SERVER"));
					} catch (UnknownHostException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					send(new Message("message", newNick, ".join", "SERVER"));
				}
			}
		}
	}
	
	public void outMsgHandler(String msgToSend)
	{
		if(!msgToSend.substring(0,1).equals("/"))
		{
			try
			{
				msgToSend = crypt.encrypt(msgToSend);
			} catch (InvalidKeyException | IllegalBlockSizeException
					| BadPaddingException | UnsupportedEncodingException
					| InvalidParameterSpecException | InvalidKeySpecException
					| NoSuchAlgorithmException | NoSuchPaddingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msgToSend = crypt.kek(msgToSend);
			send(new Message("message", sender, msgToSend, "All"));
		}else
		{
			if(msgToSend.equals("/online"))
				System.out.println(onlineMembers + " : " + onlineMembers.size());
			else if(msgToSend.substring(0).equals("/fbi"))
			{
				String pastNick = sender;
				send(new Message("signout", sender, sender, "SERVER"));
				sender = "FBI-Agent";
				send(new Message("test", "testUser", "testContent", "SERVER"));
				try
				{
					send(new Message("signup", sender, InetAddress.getLocalHost().getHostName(), "SERVER"));
					send(new Message("login", sender, InetAddress.getLocalHost().getHostName(), "SERVER"));
				} catch (UnknownHostException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//send(new Message("message", sender, ".join", "SERVER"));
				try
				{
					msgToSend = crypt.encrypt("Partyvan Incoming Boys.");
				} catch (InvalidKeyException | IllegalBlockSizeException
						| BadPaddingException | UnsupportedEncodingException
						| InvalidParameterSpecException | InvalidKeySpecException
						| NoSuchAlgorithmException | NoSuchPaddingException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msgToSend = crypt.kek(msgToSend);
				send(new Message("message", sender, msgToSend, "All"));
				String newNick = pastNick;
				onlineMembers = new ArrayList<String>();
				onlineMembers.add(pastNick);
				send(new Message("signout", sender, sender, "SERVER"));
				sender = newNick;
				send(new Message("test", "testUser", "testContent", "SERVER"));
				try
				{
					send(new Message("signup", newNick, InetAddress.getLocalHost().getHostName(), "SERVER"));
					send(new Message("login", newNick, InetAddress.getLocalHost().getHostName(), "SERVER"));
				} catch (UnknownHostException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//send(new Message("message", newNick, ".join", "SERVER"));
			}else if(msgToSend.substring(0,6).equals("/nick "))
			{
				String newNick = msgToSend.substring(6);
				onlineMembers = new ArrayList<String>();
				send(new Message("signout", sender, sender, "SERVER"));
				sender = newNick;
				send(new Message("test", "testUser", "testContent", "SERVER"));
				try
				{
					send(new Message("signup", newNick, InetAddress.getLocalHost().getHostName(), "SERVER"));
					send(new Message("login", newNick, InetAddress.getLocalHost().getHostName(), "SERVER"));
				} catch (UnknownHostException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				send(new Message("message", newNick, ".join", "SERVER"));
			}
		}
	}
	
	/*public static void main(String[] args)
	{
		try
		{
			System.out.print("Nick: ");
			//sender = (new Scanner(System.in)).nextLine();
			System.out.print("Passord: ");
			//uber = new Uber(new Socket("h.nixo.no", 1337), (new Scanner(System.in)).next());
			
		} catch (IllegalArgumentException | IllegalAccessException | GeneralSecurityException | IOException
				| NoSuchFieldException | SecurityException e)
		{
			e.printStackTrace();
		}
		new Thread(uber.generalRun).start();
		new Thread(uber.outHandler).start();
			//uber.run();
		
	}*/
}
