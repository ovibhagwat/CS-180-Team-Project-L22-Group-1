import java.io.*;
import java.net.*;
import java.util.Map;
/**
 * A program that implements ClientHandler class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu, Chloe Barnes, Ovi Bhagwat, Yiyang Liu
 * @version April 15, 2024
 */

/**
 * ClientHandler handles individual client socket connections.
 * It implements the Runnable interface, allowing it to be executed by a thread.
 */
public class ClientHandler implements Runnable, ClientHandlerInterface {
    private Socket socket; // Socket to communicate with the client
    private int threadNum; // Identifier for the client thread

    /**
     * Constructs a new ClientHandler with a specific client socket and thread number.
     * @param socket the socket connecting to the client
     * @param threadNum the identifier for this handler thread
     */
    public ClientHandler(Socket socket, int threadNum) {
        this.socket = socket;
        this.threadNum = threadNum;
    }

    /**
     * The run method is called when the thread starts.
     * It handles all I/O communication with the connected client.
     */
    public void run() {
        try {
            // The following code is just for test multiple client connections
            // begin of test code
            System.out.println("ClientHandler started for client #" + threadNum);
            // end of test code

            // Initialize input and output streams
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            // Continuously handle requests from the client
            while (true) {
                try {
                    // Receive the request from the client
                    RequestResponseProtocol.Request request = (RequestResponseProtocol.Request)
                            inputStream.readObject();

                    // Process the request based on its type
                    RequestResponseProtocol.Response response = processRequest(request);

                    // Send the response back to the client
                    outputStream.writeObject(response);
                    outputStream.flush();
                } catch (EOFException e) {
                    System.out.println("Client #" + threadNum + " has closed the connection.");
                    break; // Exit the loop and end this thread
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the socket when the client disconnects
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Processes individual client requests based on the request type.
     * @param request the client's request
     * @return the response based on the processed request
     */
    public RequestResponseProtocol.Response processRequest(RequestResponseProtocol.Request request) {
        // Switch statement to handle different request types
        switch (request.getType()) {
            case LOGIN:
                // Process login request
                return processLoginRequest(request);
            case CREATE_ACCOUNT:
                // Process create new account request
                return processCreateAccountRequest(request);
            case CHANGE_USER_NAME:
                // Process change username request
                return processChangeUserNameRequest(request);
            case CHANGE_PROFILE:
                // Process change profile request
                return processChangeProfileRequest(request);
            case ADD_FRIEND:
                // Process add friend request
                return processAddFriendRequest(request);
            case REMOVE_FRIEND:
                // Process remove friend request
                return processRemoveFriendRequest(request);
            case BLOCK_USER:
                // Process block user request
                return processBlockRequest(request);
            case UNBLOCK_USER:
                // Process unblock user request
                return processUnblockRequest(request);
            case CREATE_CONVERSATION:
                return processCreateConversationRequest(request);
            case SEND_MESSAGE:
                // Process send a message request
                return processSendMessageRequest(request);
            case SEND_PHOTO_MESSAGE:
                return processSendPhotoMessageRequest(request);
            case DELETE_MESSAGE:
                // Process delete a message request
                return processDeleteMessageRequest(request);
            case FETCH_MESSAGES:
                // Process fetch messages request
                return processFetchMessagesRequest(request);
            case TEST:
                return processTestRequest(request);
            default:
                // Handle unrecognized request types
                return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                        RequestResponseProtocol.ErrorCode.INVALID_REQUEST);
        }
    }

    /**
     * Processes a login request from a client.
     * Validates the credentials against the database and returns the appropriate response.
     *
     * @param request The request containing login details.
     * @return The response indicating the success or failure of the login attempt.
     */
    public RequestResponseProtocol.Response processLoginRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        String accountID = (String) params.get("accountID");
        String password = (String) params.get("password");
        try {
            // Attempt to login with provided credentials
            User user = DatabaseManage.login(accountID, password);
            // Return user data if login is successful
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.DATA, user);
        } catch (AccountErrorException e) {
            // Handle situation where the account ID does not exist
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.USER_NOT_FOUND);
        } catch (PasswordErrorException e) {
            // Handle incorrect password entry
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INCORRECT_PASSWORD);
        } catch (Exception e) {
            // Handle general errors that may occur during the login process
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes a request to create a new user account.
     * Attempts to create an account and returns the outcome.
     *
     * @param request The request containing the account creation details.
     * @return The response indicating the success or failure of account creation.
     */
    public RequestResponseProtocol.Response processCreateAccountRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        String accountID = (String) params.get("accountID");
        String password = (String) params.get("password");

        try {
            // Attempt to create an account
            User user = DatabaseManage.createAccount(accountID, password);
            // If account creation is successful, return data response carrying the user back to client
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.DATA, user);
        } catch (AccountErrorException e) {
            // Handle situation where the account already exists
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.USER_ALREADY_EXISTS);
        } catch (PasswordErrorException e) {
            // Handle invalid password format or criteria failure
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INVALID_PASSWORD);
        } catch (Exception e) {
            // Handle general errors that may occur during the account creation process
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes a request to change a user's username.
     * Updates the username in the system and returns the result.
     *
     * @param request The request with the new username information.
     * @return The response indicating the success or failure of the username change.
     */
    public RequestResponseProtocol.Response processChangeUserNameRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        User user = (User) params.get("user");
        String newName = (String) params.get("newName");
        try {
            // Attempt to change the user's username
            user.changeUserName(newName);
            // Return success if username is changed successfully
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        } catch (Exception e) {
            // Handle general errors that may occur during the username change process
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes a profile change request.
     * Updates user profile details based on the provided new profile information.
     *
     * @param request Contains user information and new profile details to update.
     * @return Response indicating the success or error status of the profile update operation.
     */
    public RequestResponseProtocol.Response processChangeProfileRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        User user = (User) params.get("user");
        String newProfile = (String) params.get("newProfile");
        try {
            user.changeUserProfile(newProfile);
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        } catch (Exception e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes a friend addition request.
     * Adds a new friend to the user's friend list if the friend account exists and is not already added.
     *
     * @param request Contains user information and the account ID of the friend to add.
     * @return Response indicating the success or error status of the friend addition operation.
     */
    public RequestResponseProtocol.Response processAddFriendRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        User user = (User) params.get("user");
        String friendAccountID = (String) params.get("friendAccountID");
        if (!DatabaseManage.accountExist(friendAccountID)) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.USER_NOT_FOUND);
        }
        try {
            user.addFriend(friendAccountID);
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        } catch (FriendBlockErrorException e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.FRIEND_ADDED);
        } catch (Exception e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Processes a friend removal request.
     * Removes a friend from the user's friend list based on the provided account ID.
     *
     * @param request Contains user information and the account ID of the friend to remove.
     * @return Response indicating the success or error status of the friend removal operation.
     */
    public RequestResponseProtocol.Response processRemoveFriendRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        User user = (User) params.get("user");
        String friendAccountID = (String) params.get("friendAccountID");

        try {
            user.removeFriend(friendAccountID);
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        } catch (FriendBlockErrorException e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.FRIEND_REMOVED);
        } catch (Exception e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes a user block request.
     * Blocks a specified user account from interacting or being visible to the requesting user.
     *
     * @param request Contains user information and the account ID to block.
     * @return Response indicating the success or error status of the block operation.
     */
    public RequestResponseProtocol.Response processBlockRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> parameters = request.getParameters();
        User user = (User) parameters.get("user");
        String blockAccountID = (String) parameters.get("blockAccountID");
        try {
            user.addBlock(blockAccountID);
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        } catch (FriendBlockErrorException e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.USER_BLOCKED);
        } catch (Exception e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes a user unblock request.
     * Unblocks a previously blocked user account, allowing interaction and visibility again.
     *
     * @param request Contains user information and the account ID to unblock.
     * @return Response indicating the success or error status of the unblock operation.
     */
    public RequestResponseProtocol.Response processUnblockRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> parameters = request.getParameters();
        User user = (User) parameters.get("user");
        String blockAccountID = (String) parameters.get("blockAccountID");
        try {
            user.removeBlock(blockAccountID);
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        } catch (FriendBlockErrorException e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.USER_NOT_BLOCKED);
        } catch (Exception e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes a message sending request.
     * Sends a message from the user to another party within a conversation.
     *
     * @param request Contains conversation details and the message to send.
     * @return Response indicating the success or error status of the message sending operation.
     */

    public RequestResponseProtocol.Response processCreateConversationRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        User sender = (User) params.get("sender");
        User receiver = (User) params.get("receiver");
        try {
            Conversation c = DatabaseManage.createConversation(sender, receiver);
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.DATA, c);
        } catch (MessageSendErrorException e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.USER_BLOCKED);
        } catch (Exception e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    public RequestResponseProtocol.Response processSendMessageRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        Conversation conversation = (Conversation) params.get("conversation");
        Message message = (Message) params.get("message");

        try {
            // Attempt to add message
            conversation.addMessage(message);
            // If message sending is successful, return success response
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        } catch (MessageSendErrorException e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.USER_BLOCKED);
        } catch (Exception e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public RequestResponseProtocol.Response processSendPhotoMessageRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        Conversation conversation = (Conversation) params.get("conversation");
        PhotoMessage message = (PhotoMessage) params.get("photoMessage");

        try {
            // Attempt to add message
            conversation.addMessage(message);
            message.writePhotoMessage();
            // If message sending is successful, return success response
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        } catch (MessageSendErrorException e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.USER_BLOCKED);
        }
    }

    /**
     * Processes a message deletion request.
     * Removes a specified message from a conversation.
     *
     * @param request Contains conversation details and the message to delete.
     * @return Response indicating the success or error status of the message deletion operation.
     */
    public RequestResponseProtocol.Response processDeleteMessageRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        Conversation conversation = (Conversation) params.get("conversation");
        Message message = (Message) params.get("message");

        try {
            // Attempt to add message
            conversation.deleteMessage(message);
            // If message sending is successful, return success response
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.SUCCESS);
        } catch (MessageSendErrorException e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.PERMISSION_DENIED);
        } catch (Exception e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes a message fetching request.
     * Retrieves all messages from a specific conversation.
     *
     * @param request Contains details of the conversation from which to fetch messages.
     * @return Response containing the conversation data or an error status.
     */
    public RequestResponseProtocol.Response processFetchMessagesRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        Conversation conversation = (Conversation) params.get("conversation");
        try {
            conversation.readMessages();
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.DATA, conversation);
        } catch (Exception e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes a test request.
     * This method is typically used for debugging or testing the functionality of the request processing system.
     *
     * @param request Contains arbitrary test data for processing.
     * @return Response containing test data or an error status.
     */
    public RequestResponseProtocol.Response processTestRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        String information = (String) params.get("information");
        try {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.DATA, information);
        } catch (Exception e) {
            return new RequestResponseProtocol.Response(RequestResponseProtocol.ResponseType.ERROR,
                    RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
