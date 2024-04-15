import java.net.*;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket socket;
    private int threadNum;
    public ClientHandler(Socket socket, int threadNum) {
        this.socket = socket;
        this.threadNum = threadNum;
    }
    public void run() {

    }

    private RequestResponseProtocol.Response processRequest(RequestResponseProtocol.Request request) {
        // Switch statement to handle different request types
        switch (request.getType()) {
            case LOGIN:
                // Process login request
                return processLoginRequest(request);
            case LOGOUT:
                // Process fetch data request
                return processLogoutRequest(request);
            case CREATE_ACCOUNT:
                // Process create new account request
                return processCreateAcctRequest(request);
            case SEND_MESSAGE:
                // Process send a message request
                return processSendMsgRequest(request);
            case BLOCK_USER:
                // Process block user request
                return processBlockRequest(request);
            case UNBLOCK_USER:
                // Process unblock user request
                return processUnblockRequest(request);
            default:
                // Handle unrecognized request types
                return createErrorResponse(RequestResponseProtocol.ErrorCode.INVALID_REQUEST);
        }
    }

    private RequestResponseProtocol.Response processLoginRequest(RequestResponseProtocol.Request request) {
        // Your logic to handle login request
    }

    // Method to process fetch data request
    private RequestResponseProtocol.Response processLogoutRequest(RequestResponseProtocol.Request request) {
        // Your logic to handle fetch data request  
    }

    private RequestResponseProtocol.Response processAddFriendRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        User user = (User) params.get("user");
        String friendAccountID = (String) params.get("friendAccountID");

        User friend = new User(friendAccountID);

        if (user == null || friend == null) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.USER_NOT_FOUND);
        }

        try {
            user.addFriend(friendAccountID);
            RequestResponseProtocol.Response response = new RequestResponseProtocol().new Response();
            response.setType(RequestResponseProtocol.ResponseType.SUCCESS);
            return response;
        } catch (FriendBlockErrorException e) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.PERMISSION_DENIED);
        } catch (Exception e) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    private RequestResponseProtocol.Response processRemoveFriendRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        User user = (User) params.get("user");
        String friendAccountID = (String) params.get("friendAccountID");
        User friend = new User(friendAccountID);
        if (user == null || friend == null) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.USER_NOT_FOUND);
        }

        try {
            user.removeFriend(friendAccountID);
            RequestResponseProtocol.Response response = new RequestResponseProtocol().new Response();
            response.setType(RequestResponseProtocol.ResponseType.SUCCESS);
            return response;
        } catch (FriendBlockErrorException e) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.PERMISSION_DENIED);
        } catch (Exception e) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }
    
    private RequestResponseProtocal.Response processBlockRequest(RequestResponseProtocal.Request request) {
        Map<String, Object> parameters = request.getParameters();
        if (parameters != null && parameters.containsKey("accountID") && parameters.containsKey("User")) {
            try {
                parameters.get("User").addBlock(parameters.get("accountID"));
                return new RequestResponseProtocol().new Response(RequestResponseProtocol.ResponseType.SUCCESS);
            } catch (FriendBlockErrorException e) {
                return createErrorResponse(e.getErrorCode());
            }
        } else {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.INVALID_REQUEST);
        }
    }
    
    private RequestResponseProtocal.Response processUnblockRequest(RequestResponseProtocal.Request request) {
        Map<String, Object> parameters = request.getParameters();
        if (parameters != null && parameters.containsKey("accountID") && parameters.containsKey("User")) {
            try {
                parameters.get("User").removeBlock(parameters.get("accountID"));
                return new RequestResponseProtocol().new Response(RequestResponseProtocol.ResponseType.SUCCESS);
            } catch (FriendBlockErrorException e) {
                return createErrorResponse(e.getErrorCode());
            }
        } else {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.INVALID_REQUEST);
        }
    }

    private RequestResponseProtocol.Response processCreateAcctRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        String accountID = (String) params.get("accountID");
        String password = (String) params.get("password");

        if (accountID == null || password == null) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.INVALID_REQUEST);
        }

        try {
            // Attempt to create an account
            User user = createAccount(accountID, password);

            // If account creation is successful, return success response
            RequestResponseProtocol.Response response = new RequestResponseProtocol().new Response();
            response.setType(RequestResponseProtocol.ResponseType.SUCCESS);
            return response;
        } catch (AccountExistException e) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.ACCOUNT_ALREADY_EXISTS);
        } catch (PasswordInvalidException e) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.INVALID_PASSWORD);
        } catch (Exception e) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private RequestResponseProtocol.Response processSendMsgRequest(RequestResponseProtocol.Request request) {
        Map<String, Object> params = request.getParameters();
        String senderID = (String) params.get("senderID");
        String receiverID = (String) params.get("receiverID");
        String message = (String) params.get("message");

        if (senderID == null || receiverID == null || message == null) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.INVALID_REQUEST);
        }

        try {
            // Attempt to send message
            sendMessage(senderID, receiverID, message);

            // If message sending is successful, return success response
            RequestResponseProtocol.Response response = new RequestResponseProtocol().new Response();
            response.setType(RequestResponseProtocol.ResponseType.SUCCESS);
            return response;
        } catch (ReceiverNotFoundException e) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.RECEIVER_NOT_FOUND);
        } catch (ReceiverBlockedException e) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.RECEIVER_BLOCKED);
        } catch (Exception e) {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // Method to create an error response
    private RequestResponseProtocol.Response createErrorResponse(RequestResponseProtocol.ErrorCode errorCode) {
        RequestResponseProtocol.Response response = new RequestResponseProtocol().new Response();
        response.setType(RequestResponseProtocol.ResponseType.ERROR);
        response.setErrorCode(errorCode);
        return response;
    }
}

