package assignment7;

import sun.plugin2.message.Conversation;
import sun.plugin2.message.Message;
import sun.plugin2.message.Serializer;

import java.io.IOException;

public class BidMessage extends Message {

    public BidMessage(int i, Conversation conversation) {
        super(i, conversation);
    }

    @Override
    public void writeFields(Serializer serializer) throws IOException {

    }

    @Override
    public void readFields(Serializer serializer) throws IOException {

    }
}
