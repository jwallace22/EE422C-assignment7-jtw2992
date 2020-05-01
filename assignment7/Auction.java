package assignment7;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;

public class Auction extends Observable{
    private ArrayList<Item> myItems = new ArrayList<>();
    ArrayList<ItemThread> threads = new ArrayList<>();
    public Auction(String fileName) throws Exception{
        File file = new File(getClass().getResource(fileName).toString().replace("file:/",""));
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            myItems.add(new Item(line[0],Double.valueOf(line[1]),Integer.valueOf(line[2])));
        }
    }

    public void startAuction(){
        for(Item m : myItems) {threads.add(new ItemThread(m));}
        for(Thread t : threads){t.start();}
    }
    public boolean processBid(Bid newBid){
        for(Item i : myItems){
            if(i.getID().equals(newBid.getItemID())){
                return i.placeBid(newBid);
            }
        }
        System.out.println("ItemID not valid for this Bid: "+newBid.toString());
        return false;
    }
    private class Item extends Observable {
        protected String ID;
        private double currentBid = 0;
        private int timeLimit;
        protected boolean sold = false;
        private String owner = null;
        public Item(String id,double min,int time){
            ID = id;
            timeLimit=time;
            currentBid=min;
        }
        public String getID(){return ID;}
        public double getCurrentBid(){
            return currentBid;
        }
        public String getOwner(){
            return owner;
        }
        public boolean placeBid(Bid newBid){
            if(newBid.getBid() <= currentBid){return false;}
            else{
                currentBid = newBid.getBid();
                owner = newBid.getClientID();
                return true;
            }
        }

    }

    public class ItemThread extends Thread{
        private Item myItem;
        private int timer;
        public ItemThread(Item item){
            myItem = item;
            timer = myItem.timeLimit;
        }
        @Override
        public void run() {
            if(timer == 0){
                myItem.sold = true;
            }
            timer--;
            try {
                Thread.sleep(1000); //decrement the timer and wait 1 second to repeat
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

