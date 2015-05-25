package clientFiles;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TopKek.Crypto;


public class IrcClient implements Runnable
{
	private String sender;
	private String login = InetAddress.getLocalHost().getHostName();
	private static String server = "rass.nixo.no";
	private String channel = "#badnat";
	private Runnable readerFunction;
	private Runnable writeFunction;
	private BufferedWriter writer;
	private BufferedReader reader;
	private Crypto crypt;
	private ArrayList<String> userList = new ArrayList<String>();
	private String link;
	private ClientGui gui;
	
	public IrcClient(Socket socket, String pass, String sender, ClientGui gui) throws Exception
	{		
		
		System.setProperty("file.encoding","UTF-8");
		Field charset = Charset.class.getDeclaredField("defaultCharset");
    	charset.setAccessible(true);
    	charset.set(null,null);
    	
		this.sender = sender;
		this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.crypt = new Crypto(pass, "Shhhh!! MuCH NSA must BE SECTRETORNMONOERN KLASUI GDas7das7kt6RDas57rjD iuYTSYU&ARDU(AS% (IASG KslSHf dkya g,UGSdo6asfio76GIOsdt7 ASFkd");		
		this.gui = gui;
	}
	
	public void init()
	{
		writeFunction = new Runnable()
		{
			public void run()
			{
				IrcClient.this.readInput();
			}
		};
		
		readerFunction = new Runnable()
		{
			public void run()
			{
				IrcClient.this.readOutput();
			}
		};
	}
	
	public void sendCrypt(String msg)
	{
		if(msg.substring(0,1).equals("/") && !msg.substring(0,4).equals("/me "))
			command(msg);
		else
		{
			String rawMessage = msg;
			if(msg.length() > 3 && msg.substring(0,4).equals("/me "))
			{
				System.out.println("[" + getTime() + "] " + sender + " " + rawMessage.substring(4));
				gui.appendToFrame("[" + getTime() + "] " + sender + " " + rawMessage.substring(4));
			}else
			{
				System.out.println("[" + getTime() + "] " + sender + ": " + rawMessage);
				gui.appendToFrame("[" + getTime() + "] " + sender + ": " + rawMessage);
			}
			try
			{
				if(crypt.kek(crypt.encrypt(msg)).length() > 456)
				{
					msg = crypt.encrypt(msg);
				}else
					msg = crypt.kek(crypt.encrypt(msg));
				send("PRIVMSG " + channel + " :" + msg);
				//send("PING :" + rawMessage);
			} catch (GeneralSecurityException | IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	public void send(String msg)
	{
		try
		{
			writer.write(msg + "\r\n");
			writer.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void readInput()
	{
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in, "Cp1252");
		while(sc.hasNextLine())
		{
			String msgToSend = sc.nextLine();
			if(msgToSend.substring(0,1).equals("/"))
				command(msgToSend);
			else
				sendCrypt(msgToSend);
		}
	}
	
	public void readOutput()
	{
		String line = null;
		try
		{
			while((line = reader.readLine()) != null)
			{
				try
				{
					if(line.split(":")[1].split(" ")[1].equals("353"))
					{
						userList.addAll(Arrays.asList((line.split(":"))[2].split(" ")));
						userList.remove(sender);
						for(int i = 0; i < userList.size(); i++)
						{
							userList.set(i, (userList.get(i).replace("@", "")).replace("+", ""));
						}
						System.out.println("Online users: " + userList);
						gui.appendToFrame("[" + getTime() + "] Console: Online users: " + userList);
					}else if(line.split(":")[1].split(" ")[1].equals("372"))
						gui.appendToFrame("[" + getTime() + "] Server: " + checkColon(line));
				} catch (ArrayIndexOutOfBoundsException stupidError)
				{
				}
				if(line.toLowerCase().startsWith("ping "))
				{
					//System.out.println(line);
					writer.write("PONG " + line.substring(5) + "\r\n");
					writer.flush();
				}else if(line.toLowerCase().contains("pong " + server))
					System.out.println("[" + getTime() + "] " + sender + ": " + checkColon(line));
				else
				{
					//Printer rå linje
					System.out.println(line);
					String[] inMsg = line.split(":");	
					String decrypted = "";
					if(line.split(":").length > 2 && line.contains("!"))
					{						
						String[] sender = inMsg[1].split("!");
						try
						{
							decrypted = crypt.decrypt(crypt.unKek(checkColon(line)));
							if(decrypted.equals("000"))
								decrypted = crypt.decrypt(checkColon(line));
							if(!decrypted.equals("000") && !decrypted.equals("** ENCRYPTED **"))
							{
								//System.out.println("[" + getTime() + "] " + sender[0] + ": " + decrypted);
								if(decrypted.length() > 3 && decrypted.substring(0,3).equals("/me"))
									gui.appendToFrame("[" + getTime() + "] " + sender[0] + " " + decrypted.substring(4));
								else
									gui.appendToFrame("[" + getTime() + "] " + sender[0] + ": " + decrypted);
							}else if(inMsg[2].equals("#badnat") && !sender[0].equals(sender))
							{
								userList.add((sender[0].replace("@", "")).replace("+", ""));
								//System.out.println("[" + getTime() + "] " + sender[0] + " has joined " + channel + "!");
								gui.appendToFrame("[" + getTime() + "] " + sender[0] + " has joined " + channel + "!");
							}else if(line.contains("QUIT"))
							{
								userList.remove(sender[0]);
								//System.out.println("[" + getTime() + "] " + sender[0] + " has left " + channel + "!");
								gui.appendToFrame("[" + getTime() + "] " + sender[0] + " has left " + channel + "!");
							}
							if(isUrl(decrypted).size() > 0)
							{
								link = isUrl(decrypted).get(0);
							}
						} catch (GeneralSecurityException | ArrayIndexOutOfBoundsException cryptoFeil)
						{
							//send("Encrypt your messages, or GTFO");
						}
					}
					if(line.contains("MODE") && line.contains("+o") && !line.contains("PRIVMSG"))
					{
						gui.appendToFrame("[" + getTime() + "] " + inMsg[1].split("!")[0] + " has OP'd" + line.substring((line.indexOf("+"))+2));
					}else if(line.contains("MODE") && line.contains("-o") && !line.contains("PRIVMSG"))
					{
						gui.appendToFrame("[" + getTime() + "] " + inMsg[1].split("!")[0] + " has DEOP'd" + line.substring((line.indexOf("-"))+2));
					}
				}
			}
		} catch (IOException e)
		{
			System.out.println("Erraaar");
			e.printStackTrace();
		}
	}

	private static String checkColon(String line)
	{
		String[] msg = line.split(":");
		StringBuilder builderString = new StringBuilder();
		if(msg.length > 3)
		{
			for(int i = 2; i < msg.length; i++)
				builderString.append(":"+msg[i]);
		}else
			builderString.append(":"+msg[2]);
		return builderString.toString().substring(1);
	}
	
	private static String getTime()
	{
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	return sdf.format(cal.getTime());
	}

	private void command(String msg)
	{	
		String command = msg.split(" ")[0];
		try
		{
			switch(command)
			{
				case "/join":
					send("JOIN " + msg.split(" ")[1]);
					break;
				case "/nick":
					send("NICK " + msg.substring(6));
					break;
				case "/online":
					StringBuilder users = new StringBuilder();
					for(String a : userList)
						users.append(a + ", ");
					System.out.println("Online users: "+users.toString().substring(0, users.length()-1));
					gui.appendToFrame("[" + getTime() + "] Console: Online users: "+users.toString().substring(0, users.length()-1));
					break;
				case "/quit":
					send("QUIT :" + "i am quit.");
					System.exit(1);
					break;
				case "/op":
					send("MODE " + channel + " +o " + msg.split(" ")[1].substring(0));
					break;
				case "/deop":
					//System.out.println(Arrays.asList(msg.split(" ")[1].substring(0)));/
					send("MODE " + channel + " -o " + msg.split(" ")[1].substring(0));
					break;
				case "/ol":
					System.out.println("Open link: " + link);
					gui.appendToFrame("[" + getTime() + "] Console: Open link: " + link);
					if(Desktop.isDesktopSupported() && link != null)
					{
					  try
						{
							Desktop.getDesktop().browse(new URI(link));
						} catch (URISyntaxException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case "/help":
					gui.appendToFrame("/join: /join #channelname joines #channelname\n"
							+ "/nick: /nick NEWNICK changes nick to NEWNICK\n"
							+ "/online: Returns all that is online on the channel\n"
							+ "/quit: Quits the program.\n"
							+ "/op: /op NICKNAME gives OP/+o to NICKNAME\n"
							+ "/deop: /deop NICKNAME deops/-o to NICKNAME\n"
							+ "/ol: Opens last sent link\n");
					break;
				case "/glod":
					break;
				default:
					System.out.println("Unknown Command: " + msg);
					gui.appendToFrame("[" + getTime() + "] Console: Unknown Command: " + msg);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> isUrl(String input) {
        ArrayList<String> result = new ArrayList<String>();

        Pattern pattern = Pattern.compile(
            "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" + 
            "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" + 
            "|mil|biz|info|mobi|name|aero|jobs|museum" + 
            "|travel|[a-z]{2}))(:[\\d]{1,5})?" + 
            "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + 
            "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + 
            "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + 
            "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }

	@Override
	public void run()
	{

	     
		try
		{
			writer.write("NICK " + sender + "\r\n");
			writer.write("USER " + login  + " 8 * : uberBadat\r\n");
			writer.flush();
			String line = null;
			while((line = reader.readLine()) != null)
			{
				System.out.println(line);
				gui.appendToFrame(line);
				if(line.indexOf("004") >= 0)
				{
					System.out.println(line);
					gui.appendToFrame(line);
					break;
				}else if(line.indexOf("443") >= 0)
				{
					System.out.println("Nickname already in use");
					gui.appendToFrame(line);
					return;
				}
			}
			
			writer.write("JOIN " + channel + "\r\n");
			writer.flush();
		}catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		readOutput();
	}
	
	/*public static void main(String[] args)
	{
		try
		{
			IrcClient client = new IrcClient(new Socket(server, 6667), "BalleRek");
			client.init();
			new Thread(client.writeFunction).start();
			new Thread(client.readerFunction).start();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}*/

}
