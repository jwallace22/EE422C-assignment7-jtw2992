package assignment7;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
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
    private static UserDatabase users;
    private ArrayList<ClientObserver> myClients=new ArrayList<>();
    private static ObjectInputStream reader;

    public static void main (String [] args) {
        server = new Server();
        server.populateUserDatabase();
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
                writer.writeObject(myAuction);
                while(writer.getClientID()==null){Thread.sleep(1000);}//waiting to recieve username and password
                addObserver(writer);
                myClients.add(writer);
                System.out.println("got a connection");
                writer.flush();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void populateUserDatabase() {
        try {
            users = new UserDatabase("approvedUsers.txt");
        } catch (FileNotFoundException e) {
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
        private boolean initialized = false;
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
            while(!initialized){
                try {
                    String input = (String) reader.readObject();
                    String username = input.split(" ")[0];
                    String password = input.split(" ")[1];
                    if(users.verifyUser(username,password)){
                        writer.writeObject("Login success");
                        initialized=true;
                        writer.setClientID(username);
                    } else {
                        writer.writeObject("Invalid Login");
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    try {
                        reader.close();
                    }
                    catch (IOException r){
                        r.printStackTrace();
                    }
                }

            }
            while (myClients.size()>0) {
                try {
                    Object input = reader.readObject();
                    System.out.println(input);
                    Bid newBid;
                    if(input instanceof Bid) {
                        newBid = (Bid) input;
                        if (myAuction.processBid(newBid)) {
                            writer.writeObject(new String(newBid.getClientID() + " success"));
                            writer.flush();
                            setChanged();
                            notifyObservers(newBid);
                            clearChanged();
                        } else {
                            writer.writeObject(new String(newBid.getClientID() + " failed"));
                        }
                        writer.flush();
                    }
                    else{
                        //recieved exit message from client. handling gracefully to prevent errors
                        String message = (String) input;
                        if(message.split(" ")[1].equals("exit")){
                            for(ClientObserver o : myClients){
                                if(o.getClientID().equals(message.split(" ")[0])){
                                    deleteObserver(o);
                                    myClients.remove(o);
                                    writer.writeObject(message.split(" ")[0]+" stl");//stl = safe to leave
                                }
                            }
                        }
                    }
                }catch(SocketException | EOFException e){
                    System.out.println("Connection lost");
                    try {
                        reader.close();
                    }
                    catch (IOException r){
                        r.printStackTrace();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    } // end of class ClientHandler
}
