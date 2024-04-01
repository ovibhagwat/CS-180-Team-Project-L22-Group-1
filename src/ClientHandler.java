import java.net.Socket;
/**
 * This is an interface for phase 2
 */
public interface ClientHandler {
    void handleClient(Socket clientSocket);
}