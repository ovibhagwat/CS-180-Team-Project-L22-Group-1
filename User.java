import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class User {
    private String filename; // each user has a file to store user's data, "accountID.txt"
    private String accountID; // unique for every user, primary key
    private String password;
    private String userName; // can be modified by user, can be the same with other users
    private ArrayList<String> friendsList; // store accountID of each friend
    private ArrayList<String> blockList; // store accountID of each blocked user
    private ArrayList<String> messageFilenames; // each dialog has a file to store message
    private String userProfile;
    public User(String accountID, String password, String filename) {
        this.accountID = accountID;
        this.password = password;
        this.filename = filename;
        this.userName = "";
        this.friendsList = new ArrayList<String>();
        this.blockList = new ArrayList<String>();
        this.messageFilenames = new ArrayList<String>();
    }

    public User(String accountID, String filename) {
        this.accountID = accountID;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<String> friendsList) {
        this.friendsList = friendsList;
    }

    public ArrayList<String> getBlockList() {
        return blockList;
    }

    public void setBlockList(ArrayList<String> blockList) {
        this.blockList = blockList;
    }

    public ArrayList<String> getMessageFilenames() {
        return messageFilenames;
    }

    public void setMessageFilenames(ArrayList<String> messageFilenames) {
        this.messageFilenames = messageFilenames;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public void downloadUser() {
        try (BufferedReader bfr = new BufferedReader(new FileReader(getFilename()))) {
            String line = "";
            setUserName(bfr.readLine());
            line = bfr.readLine();
            if (line.equals("[]")) {
                setFriendsList(new ArrayList<>());
            } else {
                String[] friendsArray = line.split(" ");
                setFriendsList(new ArrayList<String>(Arrays.asList(friendsArray)));
            }
            line = bfr.readLine();
            if (line.equals("[]")) {
                setBlockList(new ArrayList<>());
            } else {
                String[] blockArray = line.split(" ");
                setBlockList(new ArrayList<String>(Arrays.asList(blockArray)));
            }
            if (line.equals("[]")) {
                setMessageFilenames(new ArrayList<>());
            } else {
                line = bfr.readLine();
                String[] messageArray = line.split(" ");
                setMessageFilenames(new ArrayList<String>(Arrays.asList(messageArray)));
            }
        } catch (IOException e) {
            return;
        }
    }

    public void uploadUser() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilename()))) {
            pw.println(getUserName());
            pw.println(getFriendsList());
            pw.println(getBlockList());
            pw.println(getMessageFilenames());
        } catch (IOException e) {
            return;
        }
    }

    public String toString() {
        return accountID + " " + password + " " + filename;
    }
}
