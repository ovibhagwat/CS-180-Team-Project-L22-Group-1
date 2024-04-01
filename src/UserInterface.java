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

    boolean isFriend(String user);
    ArrayList<String> getFriendsList();
    void addFriend(String accountID) throws FriendBlockErrorException;
    void removeFriend(String accountID) throws FriendBlockErrorException;

    boolean isBlocked(String user);
    ArrayList<String> getBlockList();
    void addBlock(String accountID) throws FriendBlockErrorException;
    void removeBlock(String accountID) throws FriendBlockErrorException;

    ArrayList<String> getConversationFilenames();
    void addConversationFilenames(String conversationFilename);

    void readUser();
    void writeUser();
}