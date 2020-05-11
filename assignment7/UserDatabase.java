package assignment7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;
/**  EE422C Final Project submission by
 * Jeffrey Wallace
 * jtw2992
 * 16310
 * Spring 2020
 */
public class UserDatabase {
    private HashMap<String, UserData> myDatabase = new HashMap<String,UserData>();
    UserDatabase(String fileName) throws Exception{
        //File file = new File(getClass().getResource(fileName).toString().replace("file:",""));
        //Scanner scanner = null;
        //scanner = new Scanner(file);
        BufferedReader scanner = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)));
        String newLine = null;
        while((newLine=scanner.readLine())!=null){
            String[] line = newLine.split(" ");
            myDatabase.put(line[0],new UserData(line[0],line[1]));
        }
    }
    public boolean verifyUser(String username, String password){
        if(myDatabase.get(username)!=null) {
            return myDatabase.get(username).checkPassword(password);
        }
        return false;
    }
    private class UserData{
        private String myUsername;
        private String myPassword;
        private UserData(String username, String password){
            myPassword=password;
            myUsername=username;
        }
        public boolean checkPassword(String password){return password.equals(myPassword);}
    }
}
