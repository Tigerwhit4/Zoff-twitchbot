package clientFiles;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class GuiFrame extends JFrame
{
	private static final int FRAME_WIDTH = 300;
	private static final int FRAME_HEIGHT = 400;
	
	private JLabel label;
	private ActionListener listener;
	
	public GuiFrame()
	{
		label = new JLabel("Big Java");
		add(label, BorderLayout.CENTER);
		
		listener = new ChoiceListener();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
	}
	
	class ChoiceListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			
		}
	}
}
