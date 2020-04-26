package assignment7;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Auction {
    private ArrayList<Item> myItems = new ArrayList<>();
    public Auction(String fileName) throws Exception{
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
            myItems.add(new Item(line[0],Double.valueOf(line[1]),Integer.valueOf(line[2])));
        }
    }

    public void startAuction(){
        for(Item m : myItems){

        }
    }
    private class Item{
        protected String ID;
        private double currentBid = 0;
        private int timeLimit;
        private boolean sold = false;
        private String owner = null;
        public Item(String id,double min,int time){
            ID = id;
            timeLimit=time;
            currentBid=min;
        }
        public double getCurrentBid(){
            return currentBid;
        }
        public String getOwner(){
            return owner;
        }
        public boolean placeBid(double amount, String bidder){
            if(amount <= currentBid){return false;}
            else{
                currentBid = amount;
                owner = bidder;
                return true;
            }
        }

    }

    public class ItemThread extends Thread{
        private Item myItem;
        public ItemThread(Item item){
            myItem = item;
        }
        @Override
        public void run() {
            super.run();
        }
    }
}

