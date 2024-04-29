import java.io.Serializable;
import java.util.Map;
/**
 * A program that implements RequestResponseProtocol class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */
public class RequestResponseProtocol {
    public enum RequestType {
        LOGIN,
        CREATE_ACCOUNT,
        CHANGE_USER_NAME,
        CHANGE_PASSWORD,
        CHANGE_PROFILE,
        ADD_FRIEND,
        REMOVE_FRIEND,
        BLOCK_USER,
        UNBLOCK_USER,
        CREATE_CONVERSATION,
        SEND_MESSAGE,
        SEND_PHOTO_MESSAGE,
        DELETE_MESSAGE,
        FETCH_MESSAGES,
        TEST
    }

    /**
     * A program that implements ResponseType enum
     *
     * <p>Purdue University -- CS18000 -- Spring 2024</p>
     *
     * @author Yixin Hu
     * @version April 15, 2024
     */
    public enum ResponseType {
        SUCCESS,
        ERROR,
        DATA
    }

    /**
     * A program that implements ErrorCode enum
     *
     * <p>Purdue University -- CS18000 -- Spring 2024</p>
     *
     * @author Yixin Hu
     * @version April 15, 2024
     */
    public enum ErrorCode {
        USER_ALREADY_EXISTS,
        USER_NOT_FOUND,
        INVALID_PASSWORD,
        INCORRECT_PASSWORD,
        SAME_PASSWORD,
        FRIEND_ADDED,
        FRIEND_REMOVED,
        USER_BLOCKED,
        USER_NOT_BLOCKED,
        INTERNAL_SERVER_ERROR,
        PERMISSION_DENIED,
        INVALID_REQUEST
    }

    /**
     * A program that implements Request class
     *
     * <p>Purdue University -- CS18000 -- Spring 2024</p>
     *
     * @author Yixin Hu
     * @version April 15, 2024
     */
    public static class Request implements RequestInterface, Serializable {
        private RequestResponseProtocol.RequestType type;
        private Map<String, Object> parameters; // Flexible parameter passing

        // Constructor, getters, and setters
        public Request(RequestType type, Map<String, Object> parameters) {
            this.type = type;
            this.parameters = parameters;
        }
        public RequestType getType() {
            return type;
        }

        public void setType(RequestType type) {
            this.type = type;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }

    /**
     * A program that implements Response class
     *
     * <p>Purdue University -- CS18000 -- Spring 2024</p>
     *
     * @author Yixin Hu
     * @version April 15, 2024
     */
    public static class Response implements ResponseInterface, Serializable {
        private RequestResponseProtocol.ResponseType type;
        private RequestResponseProtocol.ErrorCode errorCode; // Null if type is not ERROR
        private Object data; // Data to be returned if type is DATA

        // constructors
        public Response(ResponseType type) {
            this.type = type;
            this.errorCode = null;
            this.data = null;
        }

        public Response(ResponseType type, ErrorCode errorCode) {
            this.type = type;
            this.errorCode = errorCode;
            this.data = null;
        }

        public Response(ResponseType type, Object data) {
            this.type = type;
            this.errorCode = null;
            this.data = data;
        }

        // setters and getters
        public ResponseType getType() {
            return type;
        }

        public void setType(ResponseType type) {
            this.type = type;
        }

        public ErrorCode getErrorCode() {
            return errorCode;
        }
        public void setErrorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

    }
}
