import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain implements ServerMainInterface, Runnable {
    private ServerSocket serverSocket;
    private int threadCount = 1;

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected " + clientSocket);

                Thread t = new Thread(() -> handleClient(clientSocket));
                t.start();
                threadCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server stopped.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

    }
}
