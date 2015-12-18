package clientFiles;

import java.io.BufferedReader;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private String sender = "zoffbot";
	private Runnable readerFunction;
	//private List<String> isAllowed = new ArrayList<String>();
	private Map<String, List<String>> mods = new HashMap<String, List<String>>();
	private HashMap<String, String> channels = new HashMap<String, String>();
	final com.github.nkzawa.socketio.client.Socket socket;
	
	public TwitchBot(Socket sock) throws IOException, URISyntaxException
	{
		this.writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		
		writer.write("PASS \r\n");
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
		
		join("zoffbot", "zoffbot");
		
		socket = IO.socket("http://zoff.no:3000");
		socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT, new Emitter.Listener() {
			  @Override
			  public void call(Object... args) {
			    System.out.println("Connected");
			  }
		});
		socket.connect();

		readerFunction = new Runnable()
		{
			public void run()
			{
				TwitchBot.this.readOutput();
			}
		};
		
	}
	
	private void join(String to_join, String zoffjoin) throws IOException
	{
		if(!to_join.startsWith("#")) to_join = "#"+to_join;
		if(to_join.endsWith(" ")) to_join = to_join.substring(0, to_join.length()-1);
		writer.write("JOIN "+to_join+"\r\n");
		writer.flush();
		channels.put(to_join, zoffjoin);
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
					
					String sender = line.substring(1, line.indexOf("!"));
					String full_priv = line.substring(line.indexOf("PRIVMSG #"));
					String msg_string = full_priv.substring(full_priv.indexOf(":")+1);
					String channel = full_priv.substring(full_priv.indexOf("#"), full_priv.indexOf(":"));

					if(msg_string.startsWith("!")) botCommand(msg_string, sender, channel);
					
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
				}
			}
		} catch (IOException e)
		{
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
				join(sender, sender);
				break;
			case "!help":
				if(channel.equals("#zoffbot ")) noFormatSend("PRIVMSG " + channel + " :To make me join your channel, type '!join' or '!join [ZOFF_CHANNELNAME]', depending on what Zöff-channel you want me to join.");
				else noFormatSend("PRIVMSG "+ channel + " :To request a song, type '!request YOUTUBE_ID'. It's just that easy!");
				break;
			default:
				if(msg.startsWith("!join "))
				{
					String zoffchannel = msg.split(" ")[1];
					
					join(sender, zoffchannel);
					noFormatSend("PRIVMSG "+channel+" :Joining " + sender + "'s channel");
				}else if(msg.startsWith("!request "))
						
				{
					channel = channel.substring(1);
					channel = channel.substring(0, channel.length()-1);
					
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
						
						org.json.JSONArray array = new JSONArray();
					    array.put(song_id);
					    array.put(title);
					    array.put("");
					    array.put(duration);
					    array.put(chan);
					    socket.emit("add", array);
					    noFormatSend("PRIVMSG #"+chan+" :Adding "+title+"!");
						
					} catch (JSONException e)
					{
						noFormatSend("PRIVMSG #"+ channel + " :You tried to add a faulty ID " + sender + ".. Stop that!");
						System.err.println("Faulty ID tried to add.");
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
		try
		{
			TwitchBot bot = new TwitchBot(new Socket("irc.twitch.tv", 6667));
			new Thread(bot.readerFunction).start();
		} catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

}
