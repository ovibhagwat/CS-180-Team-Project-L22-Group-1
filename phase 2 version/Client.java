import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
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
        frame.setSize(500, 500);

        cardLayout = new CardLayout();
        panels = new JPanel(cardLayout);

        addLoginPanel();
        addAccountCreationPanel();

        frame.add(panels);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public void addLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Login"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

       // loginPanel.setBorder(BorderFactory.createTitledBorder("Login"));
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        constraints.gridx = 0;
        constraints.gridy = 0;
        loginPanel.add(new JLabel("Username"), constraints);

        constraints.gridx = 1;
        loginPanel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        loginPanel.add(new JLabel("Password"), constraints);
        constraints.gridx = 1;
        loginPanel.add(passwordField, constraints);
        JButton loginButton = new JButton("Login");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        loginPanel.add(loginButton, constraints);

        JButton switchToCreateButton = new JButton("Switch to Create Account");
        constraints.gridy = 3;
        loginPanel.add(switchToCreateButton, constraints);

        loginButton.addActionListener(e -> handleLogin());
        switchToCreateButton.addActionListener(e -> cardLayout.show(panels, "CreateAccount"));

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
        JPanel createPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        createPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Create Account"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        passwordField2 = new JPasswordField(20);

        constraints.gridx = 0;
        constraints.gridy = 0;
        createPanel.add(new JLabel("Set Username"), constraints);
        constraints.gridx = 1;
        createPanel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        createPanel.add(new JLabel("Set Password"), constraints);
        constraints.gridx = 1;
        createPanel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        createPanel.add(new JLabel("Confirm Password"), constraints);
        constraints.gridx = 1;
        createPanel.add(passwordField2, constraints);

        JButton createButton = new JButton("Create Account");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;

        createPanel.add(createButton, constraints);
        JButton switchToLoginButton = new JButton("Back to Login");
        constraints.gridy = 4;
        createPanel.add(switchToLoginButton, constraints);

        createButton.addActionListener(e -> handleCreateAccount());
        switchToLoginButton.addActionListener(e -> cardLayout.show(panels, "Login"));

        panels.add(createPanel, "CreateAccount");
    }

    public void handleCreateAccount() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();
        char[] password2 = passwordField2.getPassword();

        if (!Arrays.equals(password, password2)) {
            JOptionPane.showMessageDialog(null, "Passwords must match!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField2.setText("");
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
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

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
