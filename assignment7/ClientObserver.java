package assignment7;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver extends ObjectOutputStream implements Observer {
    private String clientID;
    public ClientObserver(OutputStream out, String id) throws IOException {
        super(out);
        clientID=id;
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
}
