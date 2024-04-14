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

    public static void handleClient(Socket clientSocket) {
        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
            String request = bfr.readLine();
            String response = processRequest(request);
            pw.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String processRequest(String request) {
        String[] parts = request.split(" ");
        String command = parts[0];
        String accountID = parts[1];
        String password = parts[2];
        
        switch (command) {
            case "LOGIN":
                try {
                    User user = DatabaseManage.login(accountID, password);
                    return "LOGIN_SUCCESS " + user.getAccountID();
                } catch (PasswordErrorException | AccountErrorException e) {
                    return "LOGIN_ERROR " + e.getMessage();
                }
            case "REGISTER":
                try {
                    User newUser = DatabaseManage.createAccount(accountID, password);
                    return "REGISTER_SUCCESS " + newUser.getAccountID();
                } catch (AccountErrorException | PasswordErrorException e) {
                    return "REGISTER_ERROR " + e.getMessage();
                }
            default:
                return "INVALID_COMMAND";
        }
    }
}
