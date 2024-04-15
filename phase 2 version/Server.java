import java.net.ServerSocket;
import java.net.Socket;
/**
 * A program that implements Server class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */

public class Server implements ServerInterface {
    private ServerSocket serverSocket; // The server socket that listens for client connections
    private boolean running; // Flag to control the server's running state

    /**
     * Starts the server to listen for incoming client connections on the specified port.
     * It accepts connections in a loop, spawns a new thread for each client using the ClientHandler class.
     *
     * @param port The port number on which the server will listen for connections.
     */
    @Override
    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port); // Initialize the ServerSocket on the specified port
            running = true; // Set the server's running state to true
            int threadCount = 1; // Counter to keep track of the number of connected clients (for thread naming)
            while (running) {
                Socket socket = serverSocket.accept(); // Accept an incoming client connection
                // Create a new thread for handling the client connection
                Thread t = new Thread(new ClientHandler(socket, threadCount));
                t.start(); // Start the new thread
                threadCount++; // Start the new thread
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print any exception stack traces to the standard error stream
        }
    }

    /**
     * Stops the server and releases the server socket.
     * This method sets the running flag to false and attempts to close the server socket.
     */
    @Override
    public void stopServer() {
        try {
            running = false; // Set the server's running state to false to exit the loop
            if (serverSocket != null) {
                serverSocket.close(); // Close the server socket to free the resource
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print any exception stack traces to the standard error stream
        }
    }

    /**
     * Main method to start the server. This serves as the entry point for the server application.
     * It creates an instance of Server and starts it on a specific port.
     *
     * @param args Command line arguments (not used in this implementation).
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer(4242); // Start the server on port 4242
    }
}
