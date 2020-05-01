package assignment7;
/*
 * Author: Vallath Nandakumar and EE 422C instructors
 * Date: April 20, 2020
 * This starter code is from the MultiThreadChat example from the lecture, and is on Canvas. 
 * It doesn't compile.
 */
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import java.util.ArrayList;

public class Client extends Application {
	// I/O streams 
	ObjectOutputStream toServer = null; 
	ObjectInputStream fromServer = null;
	@FXML private Label currentBid;
	@FXML private Label currentWinner;
	@FXML private Label feedback;
	@FXML private TextField bidAmountField;
	public static int numUsers = 0;
	private String clientID;
	private static boolean waitingForFeedback = false;
	private static boolean successfulBid = false;
	private static ArrayList<Bid> newBids = new ArrayList<>();
	public Client(){
		clientID = "user00"+String.valueOf(numUsers);
		numUsers++;
	}
	@FXML
	public void placeBid(){
		feedback.setText("");
		try{
			Double bid = Double.valueOf(bidAmountField.getText());
			newBids.add(new Bid(clientID,bid,"RunningShoes"));
			currentBid.setText(bid.toString());
			currentWinner.setText(clientID);
			waitingForFeedback=true;
			while(waitingForFeedback){Thread.sleep(1000);System.out.println(waitingForFeedback);}
			if(successfulBid){feedback.setText("Bid placed. You are the current winner!");}
			else {feedback.setText("Invalid Bid. Please try again!");}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void start(Stage primaryStage) {
		try {
			// Create a socket to connect to the server
			@SuppressWarnings("resource")
			Socket socket = new Socket("localhost", 5000);
			// Create an input stream to receive data from the server
			fromServer = new ObjectInputStream(socket.getInputStream());
			// Create an output stream to send data to the server
			toServer = new ObjectOutputStream(socket.getOutputStream());
			// Create a scene and place it in the stage
			primaryStage.setTitle("Client"); // Set the stage title
			primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("clientWindow.fxml")),1200,600)); // Place the scene in the stage
			primaryStage.show(); // Display the stage
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		Thread writerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					if(Client.newBids.size()>0){
						try {
							System.out.println("sending...");
							toServer.writeObject(Client.newBids.remove(0));
							toServer.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		Thread readerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String input;
				while (true) {
					try {
						input = fromServer.readUTF();
						System.out.println("From server: " + input);
						if (input.equals("success")) {
							successfulBid=true;
						} else {
							successfulBid=false;
						}
						setWaitingForFeedback(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		writerThread.start();
		readerThread.start();
	}
	private void setWaitingForFeedback(boolean value){waitingForFeedback=value;}
	public static void main(String[] args) {
		launch(args);
	}
}
