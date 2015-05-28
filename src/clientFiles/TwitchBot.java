package clientFiles;



import java.io.BufferedReader;
//import de.voidplus.soundcloud.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

public class TwitchBot
{
	
	private BufferedWriter writer;
	private BufferedReader reader;
	//private String sender = "kasperrt";
	private String sender = "zoffbot";
	private String channel = "#zoffbot";
	private Runnable writeFunction;
	private Runnable readerFunction;
	private List<String> isAllowed = new ArrayList<String>();
	private Map<String, List<String>> mods = new HashMap<String, List<String>>();
	private HashMap<String, String> channels = new HashMap<String, String>();
	
	public TwitchBot(Socket socket) throws IOException
	{
		this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		//writer.write("PASS oauth:lttljkjrjrvfoaha7ghtqxzq7uc443z\r\n");
		writer.write("PASS oauth:tpjk2j1f5e86yy9q5sn5g5ma7kjf0w\r\n");
		writer.write("NICK " + sender + "\r\n");
		writer.flush();
		
		String line = null;
		while((line = reader.readLine()) != null)
		{
			System.out.println(line);
			if(line.indexOf("376") >= 0)
			{
				System.out.println(line);
				break;
			}
		}
		
		writer.write("JOIN "+channel+"\r\n");
		writer.flush();
		
		writeFunction = new Runnable()
		{
			public void run()
			{
				TwitchBot.this.readInput();
			}
		};
		
		readerFunction = new Runnable()
		{
			public void run()
			{
				TwitchBot.this.readOutput();
			}
		};
		
	}
	
	private void send(String msg)
	{
		try
		{
			writer.write("PRIVMSG " + channel + " :" + msg + "\r\n");
			writer.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void readInput()
	{
		Scanner sc = new Scanner(System.in);
		while(sc.hasNextLine())
		{
			String msgToSend = sc.nextLine();
			if(msgToSend.toLowerCase().matches(".*[æøå].*"))
			{
				StringBuilder builder = new StringBuilder();
				for(int i = 0; i < msgToSend.length(); i++)
				{
					switch(msgToSend.charAt(i))
					{
						case 'Æ':
							builder.append("Ã†");
							break;
						case 'Ø':
							builder.append("Ã˜");
							break;
						case 'Å':
							builder.append("Ã…");
							break;
						case 'æ':
							builder.append("Ã¦");
							break;
						case 'ø':
							builder.append("Ã¸");
							break;
						case 'å':
							builder.append("Ã¥");
							break;
						default:
							builder.append(msgToSend.charAt(i));
					}
				}
				//System.out.println(builder.toString());
				send(builder.toString());
			}else if(msgToSend.equals("quit"))
			{
				try
				{
					System.out.println("Quitting " + channel);
					writer.write("PART " + channel + "\r\n");
					writer.flush();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(msgToSend.startsWith("raw "))
			{
				try
				{
					writer.write(msgToSend.substring(4)+"\r\n");
					writer.flush();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(msgToSend.equals("mods"))
			{	
				System.out.println(mods.get("#kasperrt"));
				System.out.println(mods);
			}else
			{
				send(msgToSend);
			}
		}
	}
	
	private void readOutput()
	{
		String line = null;
		try
		{
			while((line = reader.readLine()) != null)
			{
				System.out.println(line);
				if(line.toLowerCase().startsWith("ping "))
				{
					noFormatSend("PONG " + line.substring(5));
				}
				if(line.indexOf("PRIVMSG #") >= 0)
				{
					
					System.out.println(line);
					String sender = line.substring(1, line.indexOf("!"));
					String full_priv = line.substring(line.indexOf("PRIVMSG #"));
					String msg_string = full_priv.substring(full_priv.indexOf(":")+1);
					String channel = full_priv.substring(full_priv.indexOf("#"), full_priv.indexOf(":"));

					if(msg_string.startsWith("!")) botCommand(msg_string, sender, channel);
					
					
					/*
					String[] inMsg = line.split(":");
					if(line.split(":").length > 2 && line.contains("!"))
					{
						
						String[] sender = inMsg[1].split("!");
						StringBuilder printMsg = new StringBuilder();
						if(line.split(":").length > 3)
						{
							for(int i = 2; i < line.split(":").length; i++)
							{
								printMsg.append(inMsg[i]);
							}
						}else
							printMsg.append(inMsg[2]);
						channel = (line.substring(line.indexOf("#"), line.indexOf(" :")));
						if(((isUrl(printMsg.toString()).size() > 0 && !isAllowed.contains(sender[0])) || (printMsg.toString().length() > 300)))
						{
							noFormatSend("PRIVMSG "+ channel + " :.timeout " + sender[0]);
						}else if(isUrl(printMsg.toString()).size() > 0 && isAllowed.contains(sender[0]))
							isAllowed.remove(sender[0]);
						else if(printMsg.toString().substring(0,1).equals("!"))
							botCommand(printMsg.toString(), sender[0], channel);
						else if(channel.equals("#botrt"))
						{
							if(printMsg.toString().equals("join"))
							{
								System.out.println("Joiner "+sender[0]);
								noFormatSend("JOIN #"+sender[0]);
							}else if(printMsg.toString().equals("leave"))
							{
								System.out.println("Leaver "+sender[0]);
								noFormatSend("PART #"+sender[0]);
							}
						}
						System.out.println("[" + getTime() + "] "+ channel + " - " + sender[0] + ": " + printMsg.toString());
					}*/
				}else if(line.indexOf(":jtv MODE") >= 0 && line.contains("+o"))
				{
					List<String> tempMods = new ArrayList<String>();
					String[] modFetcher = line.split(" ");
					if(mods.containsKey(modFetcher[2]))
					{
						tempMods = mods.get(modFetcher[2]);
						tempMods.add(modFetcher[4]);
						mods.put(modFetcher[2], tempMods);
					}else
					{
						tempMods.add(modFetcher[4]);
						mods.put(modFetcher[2], tempMods);
					}
					//System.out.println(modFetcher[2]);
					//mods.put(modFetcher[2], modFetcher[4]);
				}
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void noFormatSend(String msg) throws IOException
	{
		writer.write(msg + "\r\n");
		writer.flush();
	}
	
	private void botCommand(String msg, String sender, String channel) throws IOException
	{
		switch(msg)
		{
			case "!time": 
				noFormatSend("PRIVMSG "+ channel + " :"+getTime());
				break;
			case "!creator":
				noFormatSend("PRIVMSG "+ channel + " :Kasper Rynning-Tønnesen, http://www.kasperrt.no");
				break;
			case "!join":
				System.out.println("Joining " + sender + "'s channel");
				noFormatSend("JOIN #"+sender);
				channels.put(sender, sender);
				break;
			case "!help":
				noFormatSend("PRIVMSG "+ channel + " :To request a song, type '!request YOUTUBE_ID'. It's just that easy!");
				break;
			/*case "!quit":
				if(sender.toLowerCase().equals("kasperrt"))
				{
					System.out.println("Quitting " + channel);
					try
					{
						writer.write("PART " + channel + "\r\n");
						writer.flush();
						System.exit(1);
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;*/
			default:
				if(msg.startsWith("!allow ") && sender.toLowerCase().equals("kasperrt"))
				{
					List<String> tempMods = mods.get(channel);
					if(tempMods.contains(sender))
					{
						send(sender + " is allowing --> " + msg.substring(7) + " to send a link.");
						System.out.println("Allowing " + msg.substring(7) + " to send a link");
						isAllowed.add(msg.substring(7));
					}
				}else if(msg.startsWith("!join "))
				{
					String zoffchannel = msg.split(" ")[1];
					
					channels.put(sender, zoffchannel);
					System.out.println("Joining " + sender + "'s channel");
					noFormatSend("JOIN #"+sender);
				}else if(msg.startsWith("!request "))
						
				{
					channel = channel.substring(1);
					channel = channel.substring(0, channel.length()-1);
					
					final com.github.nkzawa.socketio.client.Socket socket;
					final String chan = channels.get(channel);
					final String song_id = msg.split(" ")[1];
					final String youtube_get = "https://www.googleapis.com/youtube/v3/videos?id="+song_id+"&part=contentDetails,snippet,id&key=AIzaSyDvMlC0Kvk76-WO9UrtBaaEYyUw4z-TGqE";

					try
					{
						JSONObject youtube_json = new JSONObject(getHTML(youtube_get));
						
						JSONArray items = youtube_json.getJSONArray("items");
						JSONObject details = items.getJSONObject(0).getJSONObject("contentDetails");
						
						JSONObject video = items.getJSONObject(0);
						
						final long duration = Duration.parse(details.getString("duration")).getSeconds();
						
						final String title = video.getJSONObject("snippet").getString("title");
						
						System.out.println("id: " + song_id + "   title: " + title + "    duration: " + duration);
						
						
						socket = IO.socket("http://zoff.no:3000");
						socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT, new Emitter.Listener() {

							  @Override
							  public void call(Object... args) {
								
							    socket.emit("list", chan);
							    
							  }

							}).on(chan.toString(), new Emitter.Listener()
							{
								
								@Override
								public void call(Object... args)
								{
									// TODO Auto-generated method stub
									org.json.JSONArray array = new JSONArray();
								    array.put(song_id);
								    array.put(title);
								    array.put("");
								    array.put(duration);
								    socket.emit("add", array);
								    
								    //socket.emit("add", new );
								    socket.disconnect();
								}
							});
						
							socket.connect();
					} catch (URISyntaxException | JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
		}
	}

	private String getTime()
	{
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	return sdf.format(cal.getTime());
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
	
	public String getHTML(String urlToRead) {
	      URL url;
	      HttpURLConnection conn;
	      BufferedReader rd;
	      String line;
	      String result = "";
	      try {
	         url = new URL(urlToRead);
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         while ((line = rd.readLine()) != null) {
	            result += line;
	         }
	         rd.close();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return result;
	   }
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		try
		{
			TwitchBot bot = new TwitchBot(new Socket("irc.twitch.tv", 6667));
			new Thread(bot.writeFunction).start();
			new Thread(bot.readerFunction).start();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
