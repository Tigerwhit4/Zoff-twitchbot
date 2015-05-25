package clientFiles;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class DataStore
{
	private FileWriter fW;
	public void storeData(String type, String sender, String value)
	{
		try
		{
			fW = new FileWriter(type, true);
			fW.write(sender+":"+value+"\n");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			try
			{
				fW.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getRandomData(String type)
	{
		List<String> content = null;
		try
		{
			content = Files.readAllLines(Paths.get(type));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			return "No quotes found...";
		}
		System.out.println(content);
		return content.get((new Random()).nextInt(content.size()));
	}
	
	public String getRandomDataSender(String type, String sender)
	{
		List<String> content = null;
		try
		{
			content = Files.readAllLines(Paths.get(type));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while(content.size() > 0)
		{
			String a = content.get((new Random()).nextInt(content.size()));
			if(a.split(":")[0].equals(sender))
			{
				System.out.println(a + " i if");
				return a;
			}else
			{
				System.out.println(a + " i else");
				content.remove(a);
			}
		}
		return null;
	}
}
