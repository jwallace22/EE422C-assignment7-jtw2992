package assignment7;
/*
 * Author: Vallath Nandakumar and EE 422C instructors
 * Date: April 20, 2020
 * This starter code is from the MultiThreadChat example from the lecture, and is on Canvas. 
 * It doesn't compile.
 */

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static javafx.application.Application.launch;

public class Client extends Application {
	@FXML private Label currentBid;
	@FXML private Label currentWinner;
	@FXML private Label feedback;
	@FXML private Button bidButton;
	@FXML private TextField bidAmountField;
	public static int numUsers = 0;
	private String clientID;
	// I/O streams
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;

	public Client(){
		clientID = "user00"+String.valueOf(numUsers);
		numUsers++;
	}

	public synchronized boolean placeBid(){
		Double bid = Double.valueOf(bidAmountField.getText());
		System.out.println(bid.toString());
		try {
			System.out.println(toServer);
			toServer.writeUTF(bid.toString());
			toServer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			System.out.println("error processing:" +bid.toString());
		}
		return true;
	}
	@Override
	public void start(Stage primaryStage) {
		BorderPane mainPane = new BorderPane();

		// Create a scene and place it in the stage 
		Scene scene = new Scene(mainPane, 450, 200);
		primaryStage.setTitle("Client"); // Set the stage title 
		try {
			primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("clientWindow.fxml")),1200,600)); // Place the scene in the stage
		} catch (IOException e) {
			e.printStackTrace();
		}
		primaryStage.show(); // Display the stage
		try { 
			// Create a socket to connect to the server 
			@SuppressWarnings("resource")
			Socket socket = new Socket("localhost", 5000);

			// Create an input stream to receive data from the server 
			fromServer = new ObjectInputStream(socket.getInputStream());

			// Create an output stream to send data to the server 
			toServer = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
