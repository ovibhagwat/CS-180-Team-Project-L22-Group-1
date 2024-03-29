import java.util.ArrayList;

public interface UserInterface {
    String getAccountID();
    String getPassword();
    String getUserName();
    void setUserName(String userName);
    ArrayList<String> getFriendsList();
    void setFriendsList(ArrayList<String> friendsList);
    ArrayList<String> getBlockList();
    void setBlockList(ArrayList<String> blockList);
    ArrayList<String> getMessageFilenames();
    void setMessageFilenames(ArrayList<String> messageFilenames);
    String getUserProfile();
    void setUserProfile(String userProfile);
    void downloadUser();
    void uploadUser();

}
