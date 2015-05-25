package clientFiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.rosaloves.bitlyj.Url;
import static com.rosaloves.bitlyj.Bitly.*;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import TopKek.Crypto;


public class IrcBot
{
	private String sender;
	private String login = "Anaheim";
	private String channel = "#badnat";
	private Socket socket;
	private static Uber uber;
	private Runnable readerFunction;
	private Runnable writeFunction;
	private BufferedWriter writer;
	private BufferedReader reader;
	private Crypto crypt;
	private ArrayList<String> userList = new ArrayList<String>();
	private int bot;
	private URL url;
	private URLConnection conn;
	private PrintWriter historyWrite;
	
	
	public IrcBot(Socket socket, String pass, int bot) throws Exception
	{
		this.bot = bot;
		if(bot == 1)
			sender = "tesbot";
		else
			sender = "Fedorov";
		System.setProperty("file.encoding","no_NO.UTF-8");
		Field charset = Charset.class.getDeclaredField("defaultCharset");
    	charset.setAccessible(true);
    	charset.set(null,null);
		
		this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.crypt = new Crypto(pass, "Shhhh!! MuCH NSA must BE SECTRETORNMONOERN KLASUI GDas7das7kt6RDas57rjD iuYTSYU&ARDU(AS% (IASG KslSHf dkya g,UGSdo6asfio76GIOsdt7 ASFkd");
	    
		
		writer.write("NICK " + sender + "\r\n");
		writer.write("USER " + login  + " 8 * : uberBadnat\r\n");
		writer.flush();
		
		String line = null;
		while((line = reader.readLine()) != null)
		{
			System.out.println(line);
			if(line.indexOf("004") >= 0)
			{
				System.out.println(line);
				break;
			}else if(line.indexOf("443") >= 0)
			{
				System.out.println("Nickname already in use");
				System.exit(1);
			}
		}
		
		writer.write("JOIN " + channel + "\r\n");
		writer.flush();
	}
	
	public void init()
	{
		readerFunction = new Runnable()
		{
			public void run()
			{
				IrcBot.this.readOutput();
			}
		};
	}
	
	public void send(String msg)
	{
		String tempMsg = null;
		try
		{
			if(crypt.kek(crypt.encrypt(msg)).length() > 456)
				msg = crypt.encrypt(msg);
			else
				msg = crypt.kek(crypt.encrypt(msg));
			writer.write("PRIVMSG " + channel + " :" + msg + "\r\n");
			writer.flush();
		} catch (GeneralSecurityException | IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void sendTitle(String msg)
	{
		try
		{
			msg = TitleExtractor.getPageTitle(msg);
			System.out.println("Sending Title of link: " + msg);
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		if(msg != null)
		{
			send(msg);
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
					}
				} catch (ArrayIndexOutOfBoundsException stupidError)
				{
				}
				if(line.toLowerCase().startsWith("ping "))
				{
					writer.write("PONG " + line.substring(5) + "\r\n");
					writer.flush();
				}else
				{
					//Printer rå linje
					System.out.println(line);
					String[] inMsg = line.split(":");	
					String decrypted = "";
					if(line.split(":").length > 2 && line.contains("!"))
						try
						{							
							String[] sender = inMsg[1].split("!");
							try
							{
								decrypted = crypt.decrypt(crypt.unKek(checkColon(line)));
								if(decrypted.equals("000"))
									decrypted = crypt.decrypt(checkColon(line));
								if(!decrypted.equals("000") && !decrypted.equals("** ENCRYPTED **"))
								{
									if(decrypted.substring(0,1).equals("!") && bot == 1)
									{
										System.out.println("Firing Bot Command: " + decrypted);
										botCommand(decrypted, true, sender[0]);
									}
									String sent = "[" + getTime() + "] " + sender[0] + ": " + decrypted;
									System.out.println(sent);
									if(decrypted.contains("http://open.spotify.com/track/"))
										fetchSpotifyTitle(isUrl(decrypted).get(0));
									else if(isUrl(decrypted).size() > 0 && bot == 1)
										sendTitle(isUrl(decrypted).get(0));
									if(decrypted.contains("hore") && !decrypted.contains("!"))
										send("Nei, " + sender[0] + " er en hore!");
								}else if((sender[0].equals("Fedorov") || sender[0].equals("Nixolas1") || sender[0].equals("Nixo") || sender[0].equals("majahi")) && inMsg[2].equals("#badnat") && bot == 1)
								{
									userList.add(sender[0]);
									writer.write("MODE #badnat +o "+sender[0]+"\r\n");
									writer.flush();
								}else if(inMsg[2].equals("#badnat") && !sender[0].equals("BrillBot"))
								{
									userList.add((sender[0].replace("@", "")).replace("+", ""));
								}else if(line.contains("QUIT"))
								{
									userList.remove(sender[0]);
								}
							} catch (ArrayIndexOutOfBoundsException cryptoFeil)
							{
								//send("Encrypt your messages, or GTFO");
							}
							
						} catch (InvalidKeyException | NoSuchAlgorithmException
								| NoSuchPaddingException
								| InvalidAlgorithmParameterException
								| IllegalBlockSizeException e)
						{
							// TODO Auto-generated catch block
							//e.printStackTrace();
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
	
	//https://embed.spotify.com/?uri=spotify:track:336ZYcU6poBWi3s8yzRcAD
	
	private void fetchSpotifyTitle(String input)
	{
		String embedLink = "https://embed.spotify.com/?uri=spotify:track:"+input.substring(30);
		try
		{
			String theTitle = TitleExtractor.getPageTitle(embedLink);
			send(theTitle);
			send(tinyUrl("http://ytinstant.com/#" + theTitle.toLowerCase().replace(" ", "%20")));
			//send("youtube.com/results?search_query=" + theTitle.toLowerCase().replace(" ", "+"));
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private String tinyUrl(String urlToShorten)
	{
		Provider bitly = as("o_72lnf91pqj", "R_4e45608a46bd43f4836815c548090b63");
		return (bitly.call(shorten(urlToShorten))).getShortUrl();
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
	
	private String getTime()
	{
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	return sdf.format(cal.getTime());
	}
	
	private void botCommand(String msg, boolean decrypt, String sender)
	{
		String doneEncrypt = "";
		switch(msg)
		{
			case "!time":
				doneEncrypt = getTime();
				break;
			case "!wp":
				doneEncrypt = "http://www.kasperrt.no";
				break;
			case "!creator":
				doneEncrypt = "Den store og mektige Kasper Rynning-Tønnesen";
				break;
			case "!twitch":
				doneEncrypt = "http://twitch.tv/kasperrt";
				break;
			case "!hore":
				doneEncrypt = userList.get((new Random()).nextInt(userList.size())) + " er en hore.";
				break;
			case "!online":
				StringBuilder users = new StringBuilder();
				for(String a : userList)
					users.append(a + ", ");
				doneEncrypt = "Online users: "+users.toString().substring(0, users.length()-1);
				break;
			case "!history":
				doneEncrypt = "http://kasperrt.no/history/history.txt";
				break;
			case "!top":
				doneEncrypt = "Nå er det Gavin Free som har Tower Of Pimps.";
				break;
			case "!tits":
				if(sender.equals("Fedorov"))
					doneEncrypt = "Tits or GTFO";
				break;
			case "!yt":
				doneEncrypt = tinyUrl("http://ytinstant.com/");
				break;
			case "!op":
				try
				{
					writer.write("MODE #badnat +o Nixo\r\n");
					writer.write("MODE #badnat +o Fedorov\r\n");
					writer.write("MODE #badnat +o Nixolas1\r\n");
					writer.write("MODE #badnat +o majahi\r\n");
					writer.flush();
					doneEncrypt = "OP'ing Nixo, Nixolas1, majahi and Fedorov";
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "!quote":
				DataStore fetchData = new DataStore();
				String quote = fetchData.getRandomData("quote");
				doneEncrypt = "<Quote> " + quote.split(":")[0] + " - " +quote.split(":")[1];
				break;
			
			default:
				if(msg.startsWith("!yt "))
				{
					
					doneEncrypt = tinyUrl("http://ytinstant.com/#" + msg.substring(4).replace(" ", "%20"));
					break;
				}else if(msg.equals("!restart") && sender.equals("Fedorov"))
					System.exit(1);
				else if(msg.startsWith("!quote "))
				{
					DataStore newData = new DataStore();
					if(msg.split(" ").length > 2)
					{
						StringBuilder build = new StringBuilder();
						for(String a : msg.split(" "))
						{
							if(!a.equals(msg.split(" ")[0]) && !a.equals(msg.split(" ")[1]))
							{
								build.append(a+" ");
							}
						}
						newData.storeData("quote", msg.split(" ")[1], build.toString().substring(0, build.toString().length() - 1));
					}else
					{
						String outM = newData.getRandomDataSender("quote", msg.split(" ")[1]);
						if(outM != null)
						{
							System.out.println("i botten");
							doneEncrypt = "<Quote> " + msg.split(" ")[1] + " - " + outM.split(":")[1];
							break;
						}
					}
				}
				return;
		}
		if(decrypt)
		{
			send(doneEncrypt);
		}
	}

	public static void main(String[] args)
	{
		try
		{
			IrcBot client = new IrcBot(new Socket("rass.nixo.no", 6667), "NiggerYali", 1);
			client.init();
			new Thread(client.readerFunction).start();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
