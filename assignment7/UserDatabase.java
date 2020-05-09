package assignment7;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class UserDatabase {
    private HashMap<String, UserData> myDatabase = new HashMap<String,UserData>();
    UserDatabase(String fileName) throws FileNotFoundException{
        File file = new File(getClass().getResource(fileName).toString().replace("file:",""));
        Scanner scanner = null;
        scanner = new Scanner(file);
        while(scanner.hasNextLine()){
            String[] line = scanner.nextLine().split(" ");
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
