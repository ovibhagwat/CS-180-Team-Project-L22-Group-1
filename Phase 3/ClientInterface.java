/**
 * A program that implements Client Interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
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
     * Closes the connection to the server.
     * This method is responsible for closing any open resources associated with the connection
     * to the server, ensuring clean disconnection and resource management.
     */
    public void close();
}