import java.net.Socket;
/**
 * A program that implements ClientHandler interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */
public interface ClientHandlerInterface {
    /**
     * Processes a generic request and returns a response based on the request type.
     * This is a central method that likely calls specific methods based on the request's details.
     *
     * @param request the request from the client
     * @return the response to the request
     */
    public RequestResponseProtocol.Response processRequest(RequestResponseProtocol.Request request);

    /**
     * Processes login requests from clients, handling user authentication.
     *
     * @param request the login request containing authentication details
     * @return the response indicating success or failure of authentication
     */
    public RequestResponseProtocol.Response processLoginRequest(RequestResponseProtocol.Request request);

    /**
     * Processes account creation requests from clients.
     *
     * @param request the request containing account creation details
     * @return the response indicating the success or failure of account creation
     */
    public RequestResponseProtocol.Response processCreateAccountRequest(RequestResponseProtocol.Request request);

    /**
     * Processes requests to change a user's username.
     *
     * @param request the request with new username details
     * @return the response indicating the success or failure of the username change
     */
    public RequestResponseProtocol.Response processChangeUserNameRequest(RequestResponseProtocol.Request request);

    /**
     * Processes requests to change a user's profile information.
     *
     * @param request the request with new profile details
     * @return the response indicating the success or failure of the profile update
     */
    public RequestResponseProtocol.Response processChangeProfileRequest(RequestResponseProtocol.Request request);

    /**
     * Processes requests to add a friend to a user's friend list.
     *
     * @param request the request with friend addition details
     * @return the response indicating the success or failure of adding a friend
     */
    public RequestResponseProtocol.Response processAddFriendRequest(RequestResponseProtocol.Request request);

    /**
     * Processes requests to remove a friend from a user's friend list.
     *
     * @param request the request with details on the friend to be removed
     * @return the response indicating the success or failure of removing the friend
     */
    public RequestResponseProtocol.Response processRemoveFriendRequest(RequestResponseProtocol.Request request);

    /**
     * Processes requests to block another user.
     *
     * @param request the request with blocking details
     * @return the response indicating the success or failure of blocking the user
     */
    public RequestResponseProtocol.Response processBlockRequest(RequestResponseProtocol.Request request);

    /**
     * Processes requests to unblock a previously blocked user.
     *
     * @param request the request with unblocking details
     * @return the response indicating the success or failure of unblocking the user
     */
    public RequestResponseProtocol.Response processUnblockRequest(RequestResponseProtocol.Request request);

    /**
     * Processes requests to send a message from one user to another.
     *
     * @param request the request with message sending details
     * @return the response indicating the success or failure of sending the message
     */
    public RequestResponseProtocol.Response processSendMessageRequest(RequestResponseProtocol.Request request);

    /**
     * Processes requests to delete a message.
     *
     * @param request the request with details of the message to be deleted
     * @return the response indicating the success or failure of deleting the message
     */
    public RequestResponseProtocol.Response processDeleteMessageRequest(RequestResponseProtocol.Request request);

    /**
     * Processes requests to fetch messages from a conversation or chat.
     *
     * @param request the request with details on the messages to be fetched
     * @return the response containing the messages or an error status
     */
    public RequestResponseProtocol.Response processFetchMessagesRequest(RequestResponseProtocol.Request request);

    /**
     * Processes a test request. This method may be used for debugging or testing the request handling functionality.
     *
     * @param request the request containing test data
     * @return the response with test results or an error status
     */
    public RequestResponseProtocol.Response processTestRequest(RequestResponseProtocol.Request request);

}
