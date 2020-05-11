package assignment7;
/**  EE422C Final Project submission by
 * Jeffrey Wallace
 * jtw2992
 * 16310
 * Spring 2020
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;

public class Auction extends Observable implements Serializable {
    private int AuctionVersion = 0;
    private ArrayList<Item> myItems = new ArrayList<>();
    public Auction(String fileName) throws Exception{
        //File file = new File(getClass().getResource(fileName).toString().replace("file:",""));
        BufferedReader scanner = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)));
        //Scanner scanner = new Scanner(file);
        String newLine = null;
        while((newLine=scanner.readLine())!=null){
            String[] line = newLine.split(" ");
            String description = "";
            for(int i = 3;i<line.length;i++){description=description+" "+line[i];}
            myItems.add(new Item(line[0],Double.valueOf(line[1]),Integer.valueOf(line[2]),description));
        }
    }
    public boolean processBid(Bid newBid){
        for(Item i : myItems){
            if(i.getID().equals(newBid.getItemID())){
                AuctionVersion++;
                return i.placeBid(newBid);
            }
        }
        System.out.println("ItemID not valid for this Bid: "+newBid.toString());
        return false;
    }
    public ArrayList<Item> getAuctionItems(){
        return myItems;
    }

}

