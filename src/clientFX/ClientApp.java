package clientFX;

import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Scanner;

import clientFiles.Uber;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientApp extends Application implements clientFiles.Updater
{

	private TextField textField;
	public TextArea centerText;
	private static clientFiles.Uber uber;
	private ClientApp clientApp;
	private boolean time = false;
	private boolean listener = false;
	
	@Override
	public void start(Stage stage) throws Exception
	{
		clientApp = new ClientApp();
		// TODO Auto-generated method stub
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 500, 500);
		
		textField = new TextField("");
	    textField.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent arg0) {
	        	
	           centerText.appendText("\n"+textField.getText());
	            if(!time)
	            	clientApp.run(textField.getText());
	            else
	            	uber.outMsgHandler(textField.getText());
	            textField.setText("");
	            time = true;
	        }
	    });
	    
	    
	    
	    /*textField.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(ObservableValue<? extends String> value, String oldText, String newText) {
	            //centerText.setText(textField.getText());
	        }
	    });*/
	    //centerText = new Text(180, 180, textField.getText()); // x, y, text
	    Pane shapesPane = new Pane();
		root.setBottom(textField);
		centerText = new TextArea("Type your Nickname!");
		List<String> fonts = Font.getFamilies();
	    //centerText.setFont(new Font(fonts.get((int) (Math.random() * fonts.size())), 32));
	    shapesPane.getChildren().addAll(centerText);
	    root.setCenter(shapesPane);
		stage.setScene(scene);
		stage.setTitle("Übermenchen");
		stage.show();
	
	}

	public void toUpdate(String newText)
	{
		//final String newText1 = newText;
		c
	}
	
	
	public void run(String start)
	{
		try
		{
			//System.out.print("Nick: ");
			//sender = (new Scanner(System.in)).nextLine();
			System.out.print("Passord: ");
			System.out.println(start);
			uber = new Uber(new Socket("h.nixo.no", 1337), start, this);
			
		} catch (IllegalArgumentException | IllegalAccessException | GeneralSecurityException | IOException
				| NoSuchFieldException | SecurityException e)
		{
			e.printStackTrace();
		}
		new Thread(uber.generalRun).start();
		new Thread(uber.outHandler).start();
		//uber.run();
	}
	
	public static void main(String[] args)
	{
		launch(ClientApp.class, args);
	}

}
