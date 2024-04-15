import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * A program that implements User class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu, Yiyang Liu, Chloe Barnes
 * @version April 1, 2024
 */
public class User implements UserInterface {
    private String filename; // each user has a file to store user's data, "accountID.txt"
    private String accountID; // unique for every user, primary key
    private String password; // password for user account
    private String userName; // can be modified by user, can be the same with other users
    private ArrayList<String> friendsList; // store accountID of each friend
    private ArrayList<String> blockList; // store accountID of each blocked user
    private ArrayList<String> conversationFilenames; // each conversation has a file to store message
    private String userProfile; // one sentence to introduce the user.
    private static Object gatekeeper = new Object();

    // Constructor to create a new user with account ID, password, and filename for data storage.
    public User(String accountID, String password, String filename) {
        this.accountID = accountID;
        this.password = password;
        this.filename = filename;
        // Initialize with empty values and empty lists for friends, blocks, and conversations.
        this.userName = "";
        this.userProfile = "";
        this.friendsList = new ArrayList<String>();
        this.blockList = new ArrayList<String>();
        this.conversationFilenames = new ArrayList<String>();
    }

    // Constructor to load an existing user by account ID, automatically setting the filename for data storage.
    public User(String accountID) {
        this.accountID = accountID;
        this.filename = accountID + ".txt";
        readUser();
    }

    // Getters and setters for user properties. These methods provide controlled access to the user's data.
    public String getFilename() {
        return filename;
    }
    @Override
    public String getAccountID() {
        return accountID;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void changeUserName(String newName) {
        userName = newName;
        writeUser(); // Save changes to user data file.
    }

    public String getPassword() {
        return password;
    }
    @Override
    public void setPassword(String password) {
        this.password = password;
    }
    public void changePassword(String newPassword) throws PasswordErrorException {
        if (newPassword.equals(getPassword())) {
            throw new PasswordErrorException("You can't have the new password the same as the old one!");
        } else {
            password = newPassword;
            writeUser(); // Save changes to user data file.
        }
    }

    public String getUserProfile() {
        return userProfile;
    }
    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }
    public void changeUserProfile(String newProfile) {
        userProfile = newProfile;
        writeUser(); // Save changes to user data file.
    }

    // Friend and block list management methods.
    public boolean isFriend(String user) {
        return getFriendsList().contains(user);
    }
    @Override
    public ArrayList<String> getFriendsList() {
        return friendsList;
    }
    public void addFriend(String friendAccountID) throws FriendBlockErrorException {
        if (getFriendsList().contains(friendAccountID)) {
            throw new FriendBlockErrorException("You are already friends with this user!");
        } else {
            getFriendsList().add(friendAccountID);
        }
        writeUser(); // Save changes to user data file.
    }
    public void removeFriend(String friendAccountID) throws FriendBlockErrorException {
        if (!getFriendsList().contains(friendAccountID)) {
            throw new FriendBlockErrorException("You are not friends with this user.");
        } else {
            getFriendsList().remove(friendAccountID);
        }
        writeUser(); // Save changes to user data file.
    }

    public boolean isBlocked(String user) {
        return blockList.contains(user);
    }
    @Override
    public ArrayList<String> getBlockList() {
        return blockList;
    }
    public void addBlock(String blockAccountID) throws FriendBlockErrorException {
        if (getBlockList().contains(blockAccountID)) {
            throw new FriendBlockErrorException("You already have this user blocked!");
        } else {
            getBlockList().add(blockAccountID);
        }
        writeUser(); // Save changes to user data file.
    }
    public void removeBlock(String blockAccountID) throws FriendBlockErrorException {
        if (!getBlockList().contains(blockAccountID)) {
            throw new FriendBlockErrorException("You do not have this user blocked.");
        } else {
            getBlockList().remove(blockAccountID);
        }
        writeUser(); // Save changes to user data file.
    }

    @Override
    public ArrayList<String> getConversationFilenames() {
        return conversationFilenames;
    }
    @Override
    public void addConversationFilenames(String conversationFilename) {
        this.conversationFilenames.add(conversationFilename);
    }

    // Reads user data from file and initializes the user object with this data.
    @Override
    public void readUser() {
        try (BufferedReader bfr = new BufferedReader(new FileReader(getFilename()))) {
            String line = "";
            synchronized (gatekeeper) {
                userName = bfr.readLine();
                userProfile = bfr.readLine();
                // Process and read the user's friends list from the file.
                line = bfr.readLine();
            }
            if (line.equals("[]")) {
                friendsList = new ArrayList<>();
            } else {
                line = line.substring(1, line.length() - 1); // Remove brackets
                String[] friendsArray = line.split(", "); // Split by comma to get individual IDs
                friendsList = new ArrayList<String>(Arrays.asList(friendsArray));
            }
            synchronized (gatekeeper) {
                line = bfr.readLine();
            }
            if (line.equals("[]")) {
                blockList = new ArrayList<>();
            } else {
                line = line.substring(1, line.length() - 1);
                String[] blockArray = line.split(", ");
                blockList = new ArrayList<String>(Arrays.asList(blockArray));
            }
            synchronized (gatekeeper) {
                line = bfr.readLine();
            }
            if (line.equals("[]")) {
                conversationFilenames = new ArrayList<>();
            } else {
                line = line.substring(1, line.length() - 1);
                String[] messageArray = line.split(", ");
                conversationFilenames = new ArrayList<String>(Arrays.asList(messageArray));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Writes the current state of the user object to its data file, saving changes.
    @Override
    public void writeUser() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilename()))) {
            synchronized (gatekeeper) {
                pw.println(getUserName());
                pw.println(getUserProfile());
                pw.println(getFriendsList());
                pw.println(getBlockList());
                pw.println(getConversationFilenames());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns a string representation of the user, including account ID, password, and data filename.
    public String toString() {
        return accountID + " " + password + " " + filename;
    }
}