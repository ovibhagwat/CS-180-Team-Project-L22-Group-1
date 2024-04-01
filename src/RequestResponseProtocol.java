import java.util.Map;
/**
 * This is an interface for phase 2
 */
public interface RequestResponseProtocol {
    // Define request types
    enum RequestType {
        LOGIN,
        LOGOUT,
        CREATE_ACCOUNT,
        SEND_MESSAGE,
        FETCH_MESSAGES,
        ADD_FRIEND,
        REMOVE_FRIEND,
        BLOCK_USER,
        UNBLOCK_USER,
        CREATE_CONVERSATION
    }

    enum ResponseType {
        SUCCESS,
        ERROR,
        DATA
    }

    enum ErrorCode {
        USER_ALREADY_EXISTS,
        USER_NOT_FOUND,
        INVALID_PASSWORD,
        USER_BLOCKED,
        INTERNAL_SERVER_ERROR,
        PERMISSION_DENIED,
    }

    class Request {
        private RequestType type;
        private Map<String, Object> parameters; // Flexible parameter passing

        // Constructor, getters, and setters
    }

    // Response structure
    class Response {
        private ResponseType type;
        private ErrorCode errorCode; // Null if type is not ERROR
        private Object data; // Data to be returned if type is DATA

    }
}
