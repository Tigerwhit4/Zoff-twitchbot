package clientFiles;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileReaderTesting
{
	public static void main(String[] args) throws FileNotFoundException 
	{
		String msg = "!quote Nixolas1 horekunder har pikk i keften";
		DataStore data = new DataStore();
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
			data.storeData("quote", msg.split(" ")[1], build.toString().substring(0, build.toString().length() - 1));
		}else
		{
			
			data.getRandomData("quote");
		}
		data.getRandomData("quote");
	}
}
