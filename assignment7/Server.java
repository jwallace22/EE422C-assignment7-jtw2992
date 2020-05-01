package assignment7;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

/*
 * Author: Vallath Nandakumar and the EE 422C instructors.
 * Edited by: Jeffrey Wallace - jtw2992
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
        myAuction.startAuction();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateItems(){
        try {
            myAuction = new Auction("auctionItems.txt");
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
                this.writer = writer;//new ClientObserver(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                Bid newBid = ((Bid)reader.readObject());
                writer.writeUTF("message recieved");
                System.out.println(newBid);
                if(newBid.getBid()>5.0){
                    writer.writeObject(true);
                    System.out.println("good bid");
                }
                else{
                    writer.writeObject(false);
                    System.out.println("too low");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } // end of class ClientHandler
}
