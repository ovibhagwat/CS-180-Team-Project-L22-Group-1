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
            // Add more cases for other request types as needed
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
    
    private RequestResponseProtocal.Response processBlockRequest(RequestResponseProtocal.Request request) {
        Map<String, Object> parameters = request.getParameters();
        if (parameters != null && parameters.containsKey("accountID") && parameters.containsKey("User")) {
            try {
                parameters.get("User").addBlock(parameters.get("accountID"));
                return new RequestResponseProtocol().new Response(RequestResponseProtocol.ResponseType.SUCCESS, "User blocked successfully");
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
                return new RequestResponseProtocol().new Response(RequestResponseProtocol.ResponseType.SUCCESS, "User unblocked successfully");
            } catch (FriendBlockErrorException e) {
                return createErrorResponse(e.getErrorCode());
            }
        } else {
            return createErrorResponse(RequestResponseProtocol.ErrorCode.INVALID_REQUEST);
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

