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
    private static ArrayList<ClientObserver> myClients=new ArrayList<>();
    //private static ObjectInputStream reader;

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
                //reader = new ObjectInputStream(clientSocket.getInputStream());
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
        private ObjectInputStream reader;

        public ClientHandler(Socket clientSocket, ClientObserver writer) {
            Socket sock = clientSocket;
            try {
                reader = new ObjectInputStream(sock.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.writer = writer;
        }

        public void run() {
            boolean continueRead = true;
            while (continueRead) {
                    Object input = null;
                    try {
                        input = reader.readObject();
                    } catch (StreamCorruptedException|SocketException e ) {
                        System.out.println(writer.getClientID()+" disconnected.");
                        myClients.remove(writer);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (!initialized) {
                        if ((input instanceof String) && ((String) input).split(" ")[0].equals("login")) {
                            try {
                                String username = ((String) input).split(" ")[1];
                                String password = ((String) input).split(" ")[2];
                                if (users.verifyUser(username, password)) {
                                    writer.writeObject("Login success");
                                    initialized = true;
                                    writer.setClientID(username);
                                } else {
                                    writer.writeObject("Login failed");
                                }
                                writer.flush();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            if (input instanceof Bid) {
                                Bid newBid = (Bid) input;
                                if (myAuction.processBid(newBid)) {
                                    writer.writeObject(new String(newBid.getClientID() + " success"));
                                    writer.flush();
                                    updateLog(newBid);
                                    setChanged();
                                    notifyObservers(newBid);
                                    clearChanged();
                                } else {
                                    writer.writeObject(new String(newBid.getClientID() + " failed"));
                                    writer.flush();
                                }

                            } else if (input instanceof String) {
                                //received exit message from client. handling gracefully to prevent errors
                                String message = (String) input;
                                if (message.split(" ").length > 1 && message.split(" ")[1].equals("exit")) {
                                    for (int i = 0; i < myClients.size(); i++) {
                                        ClientObserver o = myClients.get(i);
                                        if (o.getClientID().equals(message.split(" ")[0])) {
                                            deleteObserver(o);
                                            myClients.remove(o);
                                            continueRead=false;

                                            writer.writeObject(message.split(" ")[0] + " stl");//stl = safe to leave
                                            reader.close();
                                            writer.flush();
                                        }
                                    }
                                }
                            }
                        } catch (SocketException | EOFException e) {
                            System.out.println("Connection lost");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        continueRead = myClients.size() > 0;
                        if(myClients.size()==0){
                            //closes out the program so that the jar files dont run indefinitely.
                            try {
                                writer.close();
                                reader.close();
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.exit(0);
                        }
                    }

            }

        }
    } // end of class ClientHandler
}