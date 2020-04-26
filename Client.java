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
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sun.plugin2.message.Message;
import sun.plugin2.message.Serializer;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static javafx.application.Application.launch;

public class Client extends Application {
	@FXML private Label currentBid;
	@FXML private Label currentWinner;
	@FXML private Label feedback;
	public static int numUsers = 0;
	private String clientID;
	// I/O streams
	ObjectOutputStream toServer = null; 
	ObjectInputStream fromServer = null;

	public Client(){
		clientID = "user00"+String.valueOf(numUsers);
		numUsers++;
	}

	@Override
	public void start(Stage primaryStage) {
		BorderPane mainPane = new BorderPane();

		// Create a scene and place it in the stage 
		Scene scene = new Scene(mainPane, 450, 200);
		primaryStage.setTitle("Client"); // Set the stage title 
		try {
			primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("clientwindow.fxml")),1200,600)); // Place the scene in the stage
		} catch (IOException e) {
			e.printStackTrace();
		}
		primaryStage.show(); // Display the stage 

		//XX.setOnAction(e -> {
		//});  // etc.

		try { 
			// Create a socket to connect to the server 
			@SuppressWarnings("resource")
			Socket socket = new Socket("localhost", 8000);

			// Create an input stream to receive data from the server 
			fromServer = new ObjectInputStream(socket.getInputStream());

			// Create an output stream to send data to the server 
			toServer = new ObjectOutputStream(socket.getOutputStream());
		} 
		catch (IOException ex) {
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
