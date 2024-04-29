import javax.swing.*;

/**
 * A program that implements Client Interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu, Chloe Barnes
 * @version April 15, 2024
 */
public interface ClientInterface {
    /**
     * Sends a request to the server.
     * This method is responsible for transmitting requests formatted according to the
     * RequestResponseProtocol to a server.
     *
     * @param request The request object to be sent.
     */
    public void sendRequest(RequestResponseProtocol.Request request);

    /**
     * Receives a response from the server.
     * This method waits for and retrieves a response from the server, encapsulating the
     * response in a RequestResponseProtocol.Response object.
     *
     * @return The response received from the server.
     */
    public RequestResponseProtocol.Response receiveResponse();

    /**
     * Creates and shows the initial GUI frame
     * that the user wil see when it appliation is starts
     */
    void createAndShowGUI();

    /**
     * Creates a login panel that users will see
     * when logging in
     */
    void addLoginPanel();

    /**
     * Handles the login request from the user
     */
    void handleLogin();

    /**
     * Creates a create account panel
     */
    void addAccountCreationPanel();

    /**
     * handles create account request from the user
     */
    void handleCreateAccount();

    void addChangeUsernamePanel();

    void handleChangeUsername();
    void addChangeProfilePanel();
    void handleChangeProfile();

    static void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    void addMainPanel();
    void showBlockPanel();
    void showFriendPanel();
    void addFriendPanel();
    void removeFriendPanel();
    void addBlockPanel();
    void removeBlockPanel();
    String generateConversationFilename(String userOne, String userTwo);
    void showChatPanel();
    void addMessage(Message message);
    void toggleDeleteButton(JPanel messagePanel);
    String makeHtmlMessage(Message message);
    void sendMessage();
    void sendPhotoMessage();
    void addPhotoMessage(PhotoMessage photoMessage);
    void showOriginalSizePhoto(PhotoMessage photoMessage);
    void scrollToBottom(JScrollPane scrollPane);
    void deleteMessage(JPanel messagePanel, Message message);

    /**
     * Closes the connection to the server.
     * This method is responsible for closing any open resources associated with the connection
     * to the server, ensuring clean disconnection and resource management.
     */
    public void close();
}
