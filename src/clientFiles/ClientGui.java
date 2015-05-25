package clientFiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

public class ClientGui extends JFrame
{
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 382;
	
	private static final int AREA_ROWS = 18;
	private static final int AREA_COLUMNS = 96;
	
	
	private JTextField inputMessage;
	private JButton button;
	private JTextArea outputArea;
	private boolean test = true;
	private IrcClient client;
	private JScrollPane scrollPane;
	private Font font;
	private Border border;
	private JLabel indentLabel;
	private boolean name = false;
	private boolean pass = false;
	private String setName;
	private String setPass;
	public JFrame frame;
	
	public ClientGui() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{	
		System.setProperty("file.encoding","UTF-8");
		Field charset = Charset.class.getDeclaredField("defaultCharset");
    	charset.setAccessible(true);
    	charset.set(null,null);
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(ClientGui.class.getResource("chatr.png")));

		border = BorderFactory.createLineBorder(Color.BLACK);
		font = new Font("Courier New", Font.BOLD, 14);
		outputArea = new JTextArea(AREA_ROWS, AREA_COLUMNS);
		outputArea.setText("Type your desired nickname\n");
		outputArea.setEditable(false);
		outputArea.setLineWrap(true);
		outputArea.setBorder(border);
		outputArea.setWrapStyleWord(true);
		outputArea.setBackground(Color.black);
		outputArea.setForeground(Color.GREEN);
		outputArea.setFont(font);
			
		createTextField();
		//createButton();
		createPanel();
		inputMessage.requestFocus();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
	}
	
	class AddInterestListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			sendMessage();
		}
	}
	
	private void createTextField()
	{
		indentLabel = new JLabel(" >");
		indentLabel.setForeground(Color.green);
		indentLabel.setFont(font);
		
		final int FIELD_WIDTH = 93;
		inputMessage = new JTextField(FIELD_WIDTH);
		inputMessage.setText("");
		inputMessage.setBackground(Color.black);
		inputMessage.setForeground(Color.GREEN);
		inputMessage.setFont(font);
		inputMessage.setCaretColor(Color.green);
		//inputMessage.setBorder(border);
		
		inputMessage.addKeyListener
	      (new KeyAdapter() {
	          public void keyPressed(KeyEvent e) {
	            int key = e.getKeyCode();
	            if (key == KeyEvent.VK_ENTER) {
	               sendMessage();
	               }
	            }
	          }
	       );
	}
	
	private void createButton()
	{
		button = new JButton("SEND");
		
		ActionListener listener = new AddInterestListener();
		button.addActionListener(listener);
	}
	
	private void createPanel()
	{
		JPanel panel = new JPanel();
		//panel.add(rateLabel);
		this.scrollPane = new JScrollPane(outputArea);
		UIManager.getLookAndFeelDefaults().put( "ScrollBar.thumb", Color.green );
		panel.setBackground(Color.black);
		panel.add(scrollPane);
		panel.add(indentLabel);
		panel.add(inputMessage);
		//panel.add(button);
		add(panel);
		try
		{
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 addWindowListener( new WindowAdapter() {
			   public void windowOpened( WindowEvent e ){
			        inputMessage.requestFocus();
			     }
			   } ); 
	}
	
	private void startClient()
	{
		try
		{
			client = new IrcClient(new Socket("rass.nixo.no", 6667), setPass, setName, this);
			new Thread(client).start();
			setPass = null;
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendMessage()
	{
		if(!name && !inputMessage.getText().equals(""))
		{
			setName = inputMessage.getText();
			name = true;
			outputArea.append("Type the password for crypto\n");
		}else if(!pass && !inputMessage.getText().equals(""))
		{
			setPass = inputMessage.getText();
			pass = true;
			startClient();
		}else if(!inputMessage.getText().equals("") && name && pass)
		{
			String messageOut = inputMessage.getText();
			//resultArea.append(messageOut + "\n");
			client.sendCrypt(messageOut);			
		}
		inputMessage.setText("");
		inputMessage.requestFocus();
	}
	
	public void appendToFrame(String msg)
	{
		outputArea.append(msg+"\n");
		this.toFront();
		int x;
		outputArea.selectAll();
		x = outputArea.getSelectionEnd();
		outputArea.select(x,x);
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, Exception
	{
		JFrame frame = new ClientGui();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("ubR");
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}
}
