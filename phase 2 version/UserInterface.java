import java.util.ArrayList;
/**
 * A program that implements User Interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu, Chloe Barnes
 * @version April 1, 2024
 */
public interface UserInterface {

    // Getters and setters for user properties. These methods provide controlled access to the user's data.
    String getAccountID();

    String getPassword();
    void setPassword(String password);
    void changePassword(String newPassword) throws PasswordErrorException;

    String getUserName();
    void setUserName(String userName);
    void changeUserName(String newUserName);

    String getUserProfile();
    void setUserProfile(String userProfile);
    void changeUserProfile(String userProfile);

    // Friend and block list management methods.
    boolean isFriend(String user);
    ArrayList<String> getFriendsList();
    void addFriend(String accountID) throws FriendBlockErrorException, AccountErrorException;
    void removeFriend(String accountID) throws FriendBlockErrorException;

    boolean isBlocked(String user);
    ArrayList<String> getBlockList();
    void addBlock(String accountID) throws FriendBlockErrorException;
    void removeBlock(String accountID) throws FriendBlockErrorException;

    ArrayList<String> getConversationFilenames();
    void addConversationFilenames(String conversationFilename);

    // Reads user data from file and initializes the user object with this data.
    void readUser();

    // Writes the current state of the user object to its data file, saving changes.
    void writeUser();
}