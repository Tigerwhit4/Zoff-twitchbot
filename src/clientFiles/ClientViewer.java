package clientFiles;

import javax.swing.JFrame;

public class ClientViewer
{
	public static void main(String[] args)
	{
		JFrame frame = new GuiFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("BoopChat");
		frame.setVisible(true);
	}
}
