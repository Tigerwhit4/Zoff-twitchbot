package clientFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class testPage
{

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		URL url;
		try
		{
			url = new URL("http://kasperrt.no/youtube/testjava.php?name=botrt");
		
			BufferedReader reader = null;

			try {
			    reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

			    for (String line; (line = reader.readLine()) != null;) {
			        System.out.println(line);
			    }
			} finally {
			    if (reader != null) try { reader.close(); } catch (IOException ignore) {}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
