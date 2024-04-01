/**
 * This is an interface for phase 2
 */
public interface ClientNetworkHandler {
    void sendRequest(Object request);
    Object receiveResponse();
}