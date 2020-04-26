package assignment7;
import assignment7.ClientObserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

/*
 * Author: Vallath Nandakumar and the EE 422C instructors.
 * Data: April 20, 2020
 * This starter code assumes that you are using an Observer Design Pattern and the appropriate Java library
 * classes.  Also using Message objects instead of Strings for socket communication.
 * See the starter code for the Chat Program on Canvas.  
 * This code does not compile.
 */
public class Server extends Observable {
    private static Auction myAuction;
    static Server server;

    public static void main (String [] args) {
        server = new Server();
        server.populateItems();
        server.SetupNetworking();
    }

    private void SetupNetworking() {
        int port = 5000;
        try {
            ServerSocket ss = new ServerSocket(port);
            while (true) {
                Socket clientSocket = ss.accept();
                ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
                Thread t = new Thread(new ClientHandler(clientSocket, writer));
                t.start();
                addObserver(writer);
                System.out.println("got a connection");
            }
        } catch (IOException e) {}
    }

    private void populateItems(){
        try {
            myAuction = new Auction("C:/Users/jwall/Dropbox/College/EE 422C/Final Project/assignment7/src/assignment7/auctionItems.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class ClientHandler implements Runnable {
        private ObjectInputStream reader;
        private  ClientObserver writer; // See Canvas. Extends ObjectOutputStream, implements Observer
        Socket clientSocket;

        public ClientHandler(Socket clientSocket, ClientObserver writer) {
			Socket sock = clientSocket;
            try {
                reader = new ObjectInputStream(sock.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
			//TODO insert code here
            try {
                String input = (String) reader.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    } // end of class ClientHandler
}
