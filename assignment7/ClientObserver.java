package assignment7;
/**  EE422C Final Project submission by
 * Jeffrey Wallace
 * jtw2992
 * 16310
 * Spring 2020
 */
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver extends ObjectOutputStream implements Observer {
    private String clientID = null;
    public ClientObserver(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            this.writeObject(arg);
            this.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setClientID(String id){clientID=id;}
    public String getClientID(){return clientID;}
}
