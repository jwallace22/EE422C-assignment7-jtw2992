package assignment7;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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
                writer.writeObject(myAuction);
                writer.flush();
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
            while (true) {
                try {
                    Bid newBid = ((Bid) reader.readObject());
                    System.out.println(newBid);
                    if (myAuction.processBid(newBid)) {
                        writer.writeObject(new String("success"));
                        writer.flush();
                        setChanged();
                        notifyObservers(newBid);
                        clearChanged();
                    } else {
                        writer.writeObject(new String("failed"));
                    }
                    writer.flush();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    } // end of class ClientHandler
}
