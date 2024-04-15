/**
 * A program that implements Server interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */
public interface ServerInterface {
    /**
     * Starts the server on a specified port.
     * This method should initialize all necessary resources to allow the server to accept connections
     * on the given port and handle incoming client requests.
     *
     * @param port The port number on which the server should listen for incoming connections.
     */
    void startServer(int port);

    /**
     * Stops the server and releases all resources associated with it.
     * This method should ensure that all client connections are properly closed and
     * any resources used by the server are released cleanly.
     */
    void stopServer();
}