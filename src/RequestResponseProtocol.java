import java.util.Map;

public class RequestResponseProtocol {
    enum RequestType {
        LOGIN,
        LOGOUT,
        CREATE_ACCOUNT, //ovi
        SEND_MESSAGE, //ovi
        FETCH_MESSAGES,
        ADD_FRIEND, //chloe
        REMOVE_FRIEND, //chloe
        BLOCK_USER, //yiyang
        UNBLOCK_USER, //yiyang
        CREATE_CONVERSATION
    }

    /**
     * This is response type of RequestResponseProtocol for phase 2
     * <p>Purdue University -- CS18000 -- Spring 2024</p>
     *
     * @author Yixin Hu
     * @version April 1, 2024
     */
    enum ResponseType {
        SUCCESS,
        ERROR,
        DATA
    }

    /**
     * This is error code of RequestResponseProtocol for phase 2
     * <p>Purdue University -- CS18000 -- Spring 2024</p>
     *
     * @author Yixin Hu
     * @version April 1, 2024
     */
    enum ErrorCode {
        USER_ALREADY_EXISTS,
        USER_NOT_FOUND,
        INVALID_PASSWORD,
        USER_BLOCKED,
        INTERNAL_SERVER_ERROR,
        PERMISSION_DENIED,
        INVALID_REQUEST
    }

    /**
     * This is a class of Request for phase 2
     * <p>Purdue University -- CS18000 -- Spring 2024</p>
     *
     * @author Yixin Hu
     * @version April 1, 2024
     */
    class Request {
        private RequestResponseProtocol.RequestType type;
        private Map<String, Object> parameters; // Flexible parameter passing

        // Constructor, getters, and setters
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

    // Response structure
    /**
     * This is a class of Response for phase 2
     * <p>Purdue University -- CS18000 -- Spring 2024</p>
     *
     * @author Yixin Hu
     * @version April 1, 2024
     */
    class Response {
        private RequestResponseProtocol.ResponseType type;
        private RequestResponseProtocol.ErrorCode errorCode; // Null if type is not ERROR
        private Object data; // Data to be returned if type is DATA
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
