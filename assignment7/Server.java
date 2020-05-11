package assignment7;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;
/**  EE422C Final Project submission by
 * Jeffrey Wallace
 * jtw2992
 * 16310
 * Spring 2020
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
        updateLog("******** New Auction Started ********");
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
    private void updateLog(Bid newBid){
        try {
            FileWriter myWriter = new FileWriter(new File("AuctionHistory.txt"),true);
            myWriter.write(newBid.getClientID()+" bid $"+newBid.getBid()+" on "+newBid.getItemID()+".\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateLog(String entry){
        try {
            FileWriter myWriter = new FileWriter(new File("AuctionHistory.txt"),true);
            myWriter.write(entry+"\n");
            myWriter.close();
        } catch (IOException e) {
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
                Object response = null;
                try {
                    response = reader.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if((response instanceof String)&&((String)response).split(" ")[0].equals("login")){
                    try {
                        String username = ((String)response).split(" ")[1];
                        String password = ((String)response).split(" ")[2];
                        if(users.verifyUser(username,password)){
                            writer.writeObject("Login success");
                            initialized=true;
                            writer.setClientID(username);
                        } else {
                            writer.writeObject("Login failed");
                        }
                        writer.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            boolean continueRead = true;
            while (continueRead) {
                try {
                    Object input = reader.readObject();
                    Bid newBid;
                    if(input instanceof Bid) {
                        newBid = (Bid) input;
                        if (myAuction.processBid(newBid)) {
                            writer.writeObject(new String(newBid.getClientID() + " success"));
                            writer.flush();
                            updateLog(newBid);
                            setChanged();
                            notifyObservers(newBid);
                            clearChanged();
                        } else {
                            writer.writeObject(new String(newBid.getClientID() + " failed"));
                        }
                        writer.flush();
                    }
                    else{
                        //received exit message from client. handling gracefully to prevent errors
                        String message = (String) input;
                        if(message.split(" ")[1].equals("exit")){
                            for(int i = 0;i<myClients.size();i++){
                                ClientObserver o = myClients.get(i);
                                if(o.getClientID().equals(message.split(" ")[0])){
                                    deleteObserver(o);
                                    myClients.remove(o);
                                    writer.writeObject(message.split(" ")[0]+" stl");//stl = safe to leave
                                    writer.flush();
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
                continueRead=myClients.size()>0;
            }
        }
    } // end of class ClientHandler
}
