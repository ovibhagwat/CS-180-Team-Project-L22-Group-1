import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
/**
 * A program that implements Client class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */

public class Client extends JComponent implements ClientInterface {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JTextArea messageArea;


    public Client(String serverAddress, int serverPort) {
        try {
            // Connect to the server
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server.");

            // Initialize input and output streams
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send a request to the server
    public void sendRequest(RequestResponseProtocol.Request request) {
        try {
            // Send the request object to the server
            outputStream.writeObject(request);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to receive a response from the server
    public RequestResponseProtocol.Response receiveResponse() {
        try {
            // Receive the response object from the server
            return (RequestResponseProtocol.Response) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setupGUI() {
        JFrame frame = new JFrame("Client Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(new GridLayout(3, 2));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        messageArea = new JTextArea();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(new JScrollPane(messageArea));

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();
            Map<String, Object> params = new HashMap<>();
            params.put("username", username);
            params.put("password", new String(password));
            RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.LOGIN, params);
            sendRequest(request);
            RequestResponseProtocol.Response response = receiveResponse();
            if (response != null) {
                messageArea.setText("Response: " + response.getData());
            }
        });

        frame.setVisible(true);


    }

    // Method to close the client connection
    public void close() {
        try {
            // Close the input and output streams
            outputStream.close();
            inputStream.close();
            // Close the socket
            socket.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create a client instance and connect to the server
        Client client = new Client("localhost", 4242);


        // The following code is just for test the networkIO
        // not real implement, will be modified in phase 3
        // begin test code:
        try {
            for (int i = 0; i < 5; i++) {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("information", "test " + i);
                RequestResponseProtocol.Request request = new RequestResponseProtocol.
                        Request(RequestResponseProtocol.RequestType.TEST, parameters);
                client.sendRequest(request);
                RequestResponseProtocol.Response response = client.receiveResponse();
                System.out.println(response.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        // end test code

    }
}
