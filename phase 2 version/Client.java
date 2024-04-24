import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
/**
 * A program that implements Client class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */

public class Client extends JComponent implements ClientInterface, Serializable {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private User user;
    private CardLayout cardLayout;
    private JPanel panels;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField passwordField2;
    private JButton sendButton;


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

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Welcome!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        cardLayout = new CardLayout();
        panels = new JPanel(cardLayout);

        addLoginPanel();
        addAccountCreationPanel();

        frame.add(panels);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public void addLoginPanel() {
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton switchToCreateButton = new JButton("Create Account");

        loginButton.addActionListener(e -> handleLogin());
        switchToCreateButton.addActionListener(e -> cardLayout.show(panels, "CreateAccount"));
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(switchToCreateButton);

        panels.add(loginPanel, "Login");

    }

    public void handleLogin() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();

        // Prepare request parameters
        Map<String, Object> params = new HashMap<>();
        params.put("accountID", username);
        params.put("password", new String(password));
        RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.LOGIN, params);
        sendRequest(request);
        RequestResponseProtocol.Response response = receiveResponse();
        if (response != null) {
            if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS) {
                user = (User) response.getData();
                JOptionPane.showMessageDialog(this, "Login successful!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                JOptionPane.showMessageDialog(this, "Login failed: " + response.getErrorCode(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No response from server.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        }
        // Example of switching to Profile panel after logging in:
        // cardLayout.show(panels, "Profile")
    }

    public void addAccountCreationPanel() {
        JPanel createPanel = new JPanel(new GridLayout(4, 2));
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        passwordField2 = new JPasswordField(20);
        JButton createButton = new JButton("Create Account");
        JButton switchToLoginButton = new JButton("Back to Login");

        createButton.addActionListener(e -> handleCreateAccount());
        switchToLoginButton.addActionListener(e -> cardLayout.show(panels, "Login"));

        createPanel.add(new JLabel("Set Username:"));
        createPanel.add(usernameField);
        createPanel.add(new JLabel("Set Password:"));
        createPanel.add(passwordField);
        createPanel.add(new JLabel("Confirm Password"));
        createPanel.add(passwordField2);
        createPanel.add(createButton);
        createPanel.add(switchToLoginButton);

        panels.add(createPanel, "CreateAccount");
    }

    public void handleCreateAccount() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();
        char[] password2 = passwordField2.getPassword();

        if (password != password2) {
            JOptionPane.showMessageDialog(null, "Passwords must match!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prepare request parameters
        Map<String, Object> params = new HashMap<>();
        params.put("accountID", username);
        params.put("password", new String(password));
        RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.CREATE_ACCOUNT, params);
        sendRequest(request);
        RequestResponseProtocol.Response response = receiveResponse();
        if (response != null) {
            if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS) {
                user = (User) response.getData();
                JOptionPane.showMessageDialog(this, "Login successful!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                JOptionPane.showMessageDialog(this, "Login failed: " + response.getErrorCode(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No response from server.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        }
        // Example of switching to Profile panel after creating account:
        // cardLayout.show(panels, "Profile")
    }

    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        setLookAndFeel();
        SwingUtilities.invokeLater(client::createAndShowGUI);
    }
}
