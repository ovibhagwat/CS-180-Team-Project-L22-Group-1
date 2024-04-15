import java.net.Socket;
/**
 * This is an interface for phase 2
 */
public interface ClientHandlerInterface {
    void handleClient(Socket clientSocket);
    RequestResponseProtocol.Response processLoginRequest(RequestResponseProtocol.Request request);
    RequestResponseProtocol.Response processLogoutRequest(RequestResponseProtocol.Request request);
    RequestResponseProtocol.Response processCreateAcctRequest(RequestResponseProtocol.Request request);
    RequestResponseProtocol.Response processSendMsgRequest(RequestResponseProtocol.Request request);
    RequestResponseProtocol.Response processBlockRequest(RequestResponseProtocol.Request request);
    RequestResponseProtocol.Response processUnblockRequest(RequestResponseProtocol.Request request);
    RequestResponseProtocol.Response processAddFriendRequest(RequestResponseProtocol.Request request);
    RequestResponseProtocol.Response processRemoveFriendRequest(RequestResponseProtocol.Request request);
}
