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
import java.util.Date;
import java.util.ArrayList;
import javax.swing.text.Utilities;
import javax.swing.text.BadLocationException;

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
    private JTextArea messageArea;
    private JTextField usernameField;
    private JTextField friendIDField;
    private JTextField blockIDField;
    private JTextField messageField;
    private JTextField newUsernameField;
    private JTextArea newProfileArea;
    private JButton changeUsernameButton;
    private JButton changePasswordButton;
    private JButton changeProfileButton;
    private JButton chooseFriendButton;
    private JButton modifyFriendButton;
    private JButton modifyBlockButton;
    private JButton logoutButton;
    private JButton sendButton;
    private JButton exitButton;
    private JButton deleteButton;
    private JButton profileButton;
    private JPasswordField passwordField;
    private JPasswordField passwordField2;
    private JPasswordField newPasswordField;
    private JScrollPane scrollPane;


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

    // Method to create GUI JFrame
    public void createAndShowGUI() {
        JFrame frame = new JFrame("Welcome!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        // Using cardLayout
        cardLayout = new CardLayout();
        panels = new JPanel(cardLayout);

        addLoginPanel();
        addAccountCreationPanel();
        addChangeUsernamePanel();
        addChangePasswordPanel();
        addChangeProfilePanel();
        addMainPanel();

        frame.add(panels);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    // Method to create Login Panel of GUi
    public void addLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Login"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

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

        JButton forgotPasswordButton = new JButton("Forgot Password?");
        constraints.gridy = 4;
        loginPanel.add(forgotPasswordButton, constraints);

        loginButton.addActionListener(e -> handleLogin());
        switchToCreateButton.addActionListener(e -> {
            cardLayout.show(panels, "CreateAccount");
            panels.revalidate();
            panels.repaint();
        });
        forgotPasswordButton.addActionListener(e -> cardLayout.show(panels, "ChangePassword"));

        panels.add(loginPanel, "Login");

    }

    // Method to handle login request
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
            if (response.getType() == RequestResponseProtocol.ResponseType.DATA) {
                user = (User) response.getData();
                JOptionPane.showMessageDialog(this, "Login successful!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                JFrame welcomeFrame = (JFrame) SwingUtilities.getWindowAncestor(panels);
                welcomeFrame.dispose();
                createMainGUI();
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

    // Method to make Create Account panel for GUI
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

    // Method to handle Create Account request
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
        System.out.println(response.getType());
        if (response != null) {
            if (response.getType() == RequestResponseProtocol.ResponseType.DATA) {
                user = (User) response.getData();
                System.out.println("account success");
                JOptionPane.showMessageDialog(this, "Login successful!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(panels, "Login");
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

    // Method to create Change Username panel to GUI
    public void addChangeUsernamePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        newUsernameField = new JTextField(20);
        changeUsernameButton = new JButton("Change Username");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("New Username:"), constraints);

        constraints.gridx = 1;
        panel.add(newUsernameField, constraints);

        constraints.gridy = 1;
        constraints.gridwidth = 2;
        panel.add(changeUsernameButton, constraints);

        String newUser = newUsernameField.getText();
        changeUsernameButton.addActionListener(e -> handleChangeUsername(newUser));

        panels.add(panel, "ChangeUsername");

    }

    // Method to handle change username request
    public void handleChangeUsername(String newUsername) {
        Map<String, Object> params = new HashMap<>();
        params.put("newUsername", newUsername);
        RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.CHANGE_USER_NAME, params);
        sendRequest(request);
        RequestResponseProtocol.Response response = receiveResponse();
        if (response != null) {
            if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS) {
                user = (User) response.getData();
                JOptionPane.showMessageDialog(null, "Username successfully updated.",
                        "Change Username", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update username: " + response.getErrorCode(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No response from server",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to create change password panel of GUI
    public void addChangePasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        JTextField usernameResetField = new JTextField(20);
        newPasswordField = new JPasswordField(20);
        changePasswordButton = new JButton("Reset Password");
        JButton backButton = new JButton("Back to Login");

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("Username:"), constraints);

        constraints.gridx = 1;
        panel.add(usernameResetField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("New Password:"), constraints);

        constraints.gridx = 1;
        panel.add(newPasswordField, constraints);

        constraints.gridy = 2;
        constraints.gridwidth = 2;
        panel.add(changePasswordButton, constraints);

        constraints.gridy = 3;
        panel.add(backButton, constraints);

        changePasswordButton.addActionListener(e ->
                handleChangePassword(usernameResetField.getText(), new String(newPasswordField.getPassword())));
        backButton.addActionListener(e -> cardLayout.show(panels, "Login"));

        panels.add(panel, "ChangePassword");
    }

    // Method to handle change password request
    public void handleChangePassword(String username, String newPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put("user", username);
        params.put("newPassword", newPassword);
        RequestResponseProtocol.Request request =
                new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.CHANGE_PASSWORD, params);
        sendRequest(request);
        RequestResponseProtocol.Response response = receiveResponse();
        if (response != null) {
            if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS) {
                user = (User) response.getData();
                JOptionPane.showMessageDialog(null, "Password reset successfully.",
                        "Password Reset", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to reset password: " + response.getErrorCode(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No response from server.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to create Change Profile panel of GUI
    public void addChangeProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        newProfileArea = new JTextArea(5, 20);
        changeProfileButton = new JButton("Update Profile");

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(new JScrollPane(newProfileArea), constraints);

        constraints.gridy = 1;
        panel.add(changePasswordButton, constraints);

        changeProfileButton.addActionListener(e -> handleChangeProfile(newProfileArea.getText()));

        panels.add(panel, "ChangeProfile");
    }

    // Method to handle change profile request
    public void handleChangeProfile(String newProfile) {
        Map<String, Object> params = new HashMap<>();
        params.put("newProfile", newProfile);
        RequestResponseProtocol.Request request =
                new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.CHANGE_PROFILE, params);
        sendRequest(request);
        RequestResponseProtocol.Response response = receiveResponse();
        if (response != null) {
            if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS) {
                user = (User) response.getData();
               JOptionPane.showMessageDialog(null, "Profile updated successfully.",
                       "Profile Update", JOptionPane.INFORMATION_MESSAGE);
            } else {
               JOptionPane.showMessageDialog(null, "Failed to update profile: " + response.getErrorCode(),
                       "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No response from server.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to set look and feel of GUI
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

    public void createMainGUI() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        cardLayout = new CardLayout();
        panels = new JPanel(cardLayout);
        user = new User("1234", "1234", "1234.txt");
        addMainPanel();

        frame.add(panels);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void addMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("MainPage"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        chooseFriendButton = new JButton("Start a chat!");
        mainPanel.add(chooseFriendButton, constraints);
        constraints.gridy = 2;
        modifyFriendButton = new JButton("Modify Friends");
        mainPanel.add(modifyFriendButton, constraints);
        constraints.gridy = 3;
        modifyBlockButton = new JButton("Modify your Block List");
        mainPanel.add(modifyBlockButton, constraints);
        constraints.gridy = 4;
        logoutButton = new JButton("Logout");
        mainPanel.add(logoutButton, constraints);
        logoutButton.addActionListener(e -> {
            JFrame welcomeFrame = (JFrame) SwingUtilities.getWindowAncestor(panels);
            welcomeFrame.dispose();
        });
        chooseFriendButton.addActionListener(e -> showChatPanel());
        modifyBlockButton.addActionListener(e -> showBlockPanel());
        modifyFriendButton.addActionListener(e -> showFriendPanel());

        panels.add(mainPanel, "MainPage");
    }

    public void showChatPanel() {
        JPanel chatPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        chatPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("modifyBlock"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        constraints.gridx = 0;
        chatPanel.add(new JLabel("Choose a friend to start!"), constraints);
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        JComboBox<String> friendComboBox = new JComboBox<>(user.getFriendsList().toArray(new String[0]));
        chatPanel.add(friendComboBox, constraints);
        constraints.gridy = 3;
        JButton startChatButton = new JButton("Confirm");
        chatPanel.add(startChatButton, constraints);
        constraints.gridy = 4;
        JButton returnButton = new JButton("Return to previous page");
        chatPanel.add(returnButton, constraints);
        startChatButton.addActionListener(e -> {
            String accountID = (String) friendComboBox.getSelectedItem();
            if (accountID != null && !accountID.equals("")) {

            } else {
                JOptionPane.showMessageDialog(this, "Please choose a friend!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        returnButton.addActionListener(e -> cardLayout.show(panels, "MainPage"));
        panels.add(chatPanel, "Chat");
        cardLayout.show(panels, "Chat");
    }

    public void showBlockPanel() {
        JPanel blockPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        blockPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("modifyBlock"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        JButton addBlockButton = new JButton("Add someone to Blocklist");
        blockPanel.add(addBlockButton, constraints);
        constraints.gridy = 3;
        JButton removeBlockButton = new JButton("Remove someone from Blocklist");
        blockPanel.add(removeBlockButton, constraints);
        constraints.gridy = 4;
        JButton returnButton = new JButton("Return to previous page");
        blockPanel.add(returnButton, constraints);

        addBlockButton.addActionListener(e -> addBlockPanel());
        removeBlockButton.addActionListener(e -> removeBlockPanel());
        returnButton.addActionListener(e -> cardLayout.show(panels, "MainPage"));
        panels.add(blockPanel, "Block");
        cardLayout.show(panels, "Block");
    }

    public void showFriendPanel() {
        JPanel friendPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        friendPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("modifyFriend"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        JButton addFriendButton = new JButton("Add someone to Friendlist");
        friendPanel.add(addFriendButton, constraints);
        constraints.gridy = 3;
        JButton removeFriendButton = new JButton("Remove someone from Friendlist");
        friendPanel.add(removeFriendButton, constraints);
        constraints.gridy = 4;
        JButton returnButton = new JButton("Return to previous page");
        friendPanel.add(returnButton, constraints);

        addFriendButton.addActionListener(e -> addFriendPanel());
        removeFriendButton.addActionListener(e -> removeFriendPanel());
        returnButton.addActionListener(e -> cardLayout.show(panels, "MainPage"));
        panels.add(friendPanel, "Friend");
        cardLayout.show(panels, "Friend");
    }

    public void addFriendPanel() {
        JPanel addFPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        addFPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("add a friend!"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        constraints.gridx = 0;
        constraints.gridy = 1;
        addFPanel.add(new JLabel("Please enter your friend's ID"), constraints);
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        friendIDField = new JTextField();
        addFPanel.add(friendIDField, constraints);
        constraints.gridy = 3;
        JButton confirmButton = new JButton("Confirm");
        addFPanel.add(confirmButton, constraints);
        constraints.gridy = 4;
        JButton returnButton = new JButton("Return to previous page");
        addFPanel.add(returnButton, constraints);
        confirmButton.addActionListener(e -> {
            String accountID = friendIDField.getText();
            if (!accountID.isEmpty()) {

            } else {
                JOptionPane.showMessageDialog(this, "Please enter a friend ID!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        returnButton.addActionListener(e -> cardLayout.show(panels, "Friend"));
        panels.add(addFPanel, "addF");
        cardLayout.show(panels, "addF");
    }

    public void removeFriendPanel() {
        JPanel rFPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        rFPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("remove a friend!"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        constraints.gridx = 0;
        rFPanel.add(new JLabel("Select the friend you want to remove."), constraints);
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        JComboBox<String> friendComboBox = new JComboBox<>(user.getFriendsList().toArray(new String[0]));
        rFPanel.add(friendComboBox, constraints);
        constraints.gridy = 3;
        JButton confirmButton = new JButton("Confirm");
        rFPanel.add(confirmButton, constraints);
        constraints.gridy = 4;
        JButton returnButton = new JButton("Return to previous page");
        rFPanel.add(returnButton, constraints);
        confirmButton.addActionListener(e -> {
            String accountID = (String) friendComboBox.getSelectedItem();
            if (accountID != null && !accountID.equals("")) {
                
            } else {
                JOptionPane.showMessageDialog(this, "Please choose a friend!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        returnButton.addActionListener(e -> cardLayout.show(panels, "Friend"));
        panels.add(rFPanel, "removeF");
        cardLayout.show(panels, "removeF");
    }

    public void addBlockPanel() {
        JPanel addBPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        addBPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("add a block!"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        constraints.gridx = 0;
        addBPanel.add(new JLabel("Please enter the ID you want to block"), constraints);
        constraints.gridy = 2;
        blockIDField = new JTextField();
        addBPanel.add(blockIDField, constraints);
        constraints.gridwidth = 2;
        constraints.gridy = 3;
        JButton confirmButton = new JButton("Confirm");
        addBPanel.add(confirmButton, constraints);
        constraints.gridy = 4;
        JButton returnButton = new JButton("Return to previous page");
        addBPanel.add(returnButton, constraints);
        confirmButton.addActionListener(e -> {
            String accountID = blockIDField.getText();
            if (!accountID.isEmpty()) {

            } else {
                JOptionPane.showMessageDialog(this, "Please enter a user ID!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        returnButton.addActionListener(e -> cardLayout.show(panels, "Block"));
        panels.add(addBPanel, "addB");
        cardLayout.show(panels, "addB");
    }

    public void removeBlockPanel() {
        JPanel rBPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        rBPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("add a friend!"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        constraints.gridx = 0;
        rBPanel.add(new JLabel("Please select the ID you want to remove from block"), constraints);
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        JComboBox<String> friendComboBox = new JComboBox<>(user.getBlockList().toArray(new String[0]));
        rBPanel.add(friendComboBox, constraints);
        constraints.gridy = 3;
        JButton confirmButton = new JButton("Confirm");
        rBPanel.add(confirmButton, constraints);
        constraints.gridy = 4;
        JButton returnButton = new JButton("Return to previous page");
        rBPanel.add(returnButton, constraints);
        confirmButton.addActionListener(e -> {
            String accountID = (String) friendComboBox.getSelectedItem();
            if (accountID != null && !accountID.equals("")) {

            } else {
                JOptionPane.showMessageDialog(this, "Please choose a user!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        returnButton.addActionListener(e -> cardLayout.show(panels, "Block"));
        panels.add(rBPanel, "rB");
        cardLayout.show(panels, "rB");
    }

    public void openMessageGui(User receiveUser) {
        String receiveName = receiveUser.getUserName();
        JFrame frame = new JFrame(receiveName);
        Conversation conversation = new Conversation(user, receiveUser);
        conversation.readMessages();
        //initialize the conversation and read the saved previous messages.

        ArrayList<Message> messages = conversation.getMessages();
        int counts = messages.size();
        messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setEditable(false);
        messageArea.setWrapStyleWord(true);
        Insets insets = messageArea.getInsets();
        Insets margin = new Insets(insets.top, insets.left, insets.bottom, insets.right);
        messageArea.setMargin(margin);
        for (int i = 0; i < counts; i++) {
            String sender = messages.get(i).getSender();
            String contentBefore = messages.get(i).getContent();
            Date date = messages.get(i).getTimestamp();
            String previousMessage = sender + ": \n" + contentBefore + "\n" + date + "\n\n";
            messageArea.append(previousMessage);
        }
        //load the previous message to the messageArea.

        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        messageField = new JTextField();
        sendButton = new JButton("Send");
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.add(messageField, BorderLayout.CENTER);
        panel1.add(sendButton, BorderLayout.EAST);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel1, BorderLayout.SOUTH);
        JPanel panel2 = new JPanel();
        exitButton = new JButton("<- Back");
        deleteButton = new JButton("Delete");
        profileButton = new JButton("Profile");
        panel2.setLayout(new BorderLayout());
        JPanel panel4 = new JPanel();
        panel4.add(deleteButton);
        panel4.add(profileButton);
        panel2.add(exitButton, BorderLayout.WEST);
        panel2.add(panel4, BorderLayout.EAST);
        frame.add(panel2, BorderLayout.NORTH);
        frame.setVisible(true);
        //add buttons, the place for your messageInput, and the area to show the conversation to the JFrame.

        sendButton.addActionListener(e -> {
            String content = messageField.getText();
            if (content != null) {
                Message message = new Message("text", user.getAccountID(), receiveName, content);
                Map<String, Object> params = new HashMap<>();
                params.put("conversation", conversation);
                params.put("message", message);
                RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.SEND_MESSAGE, params);
                sendRequest(request);
                RequestResponseProtocol.Response response = receiveResponse();
                if (response != null) {
                    if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                        JOptionPane.showMessageDialog(this, "Failed to send message: " + response.getErrorCode(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS){
                        int size = conversation.getMessages().size();
                        String sender = messages.get(size - 1).getSender();
                        String contentBefore = messages.get(size - 1).getContent();
                        Date date = messages.get(size - 1).getTimestamp();
                        String messageNow = sender + ": \n" + contentBefore + "\n" + date + "\n\n";
                        messageArea.append(messageNow);
                    }
                    //print the message if it is sent successfully or throw an error message.
                }
            }
        });

        deleteButton.addActionListener(e -> deleteMessage());

        exitButton.addActionListener(e -> {

        });

        profileButton.addActionListener(e -> {
            JFrame profile = new JFrame(receiveName);
            profile.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            profile.setSize(800, 600);
            JTextArea text = new JTextArea();
            text.setLineWrap(true);
            text.setEditable(false);
            text.setWrapStyleWord(true);
            Insets inset = messageArea.getInsets();
            Insets margins = new Insets(inset.top, inset.left, inset.bottom, inset.right);
            text.setMargin(margins);
            text.append(receiveUser.getUserProfile());
            JScrollPane scroll = new JScrollPane(text);
            JPanel panel3 = new JPanel();
            panel3.setLayout(new BorderLayout());
            panel3.add(scroll);
            profile.add(panel3, BorderLayout.CENTER);
            profile.setVisible(true);
        });
        frame.setVisible(true);
    }

    private void deleteMessage() {
        int selectionStart = messageArea.getSelectionStart();
        int selectionEnd = messageArea.getSelectionEnd();

        if (selectionStart != -1 && selectionEnd != -1) {
            String text = messageArea.getText();

            // Adjust start position to include any leading whitespace or empty lines
            while (selectionStart > 0 && (text.charAt(selectionStart - 1) == '\n' || text.charAt(selectionStart - 1) == ' ' || text.charAt(selectionStart - 1) == '\t')) {
                selectionStart--;
            }

            // Adjust end position to include any trailing whitespace or empty lines
            while (selectionEnd < text.length() && (text.charAt(selectionEnd) == '\n' || text.charAt(selectionEnd) == ' ' || text.charAt(selectionEnd) == '\t')) {
                selectionEnd++;
            }

            // Check if selection starts at the beginning of a line
            boolean startsAtLineStart = selectionStart == 0 || text.charAt(selectionStart - 1) == '\n';

            // Check if selection ends at the end of a line
            boolean endsAtLineEnd = selectionEnd == text.length() || text.charAt(selectionEnd) == '\n';

            // Modify newText based on whether selection starts at the beginning of a line
            String newText;
            if (startsAtLineStart && !endsAtLineEnd) {
                newText = text.substring(selectionEnd); // Exclude the selected text without appending a newline
            } else {
                newText = text.substring(0, selectionStart) + "\n" + text.substring(selectionEnd);
            }

            messageArea.setText(newText);

            // Set caret position to the start of the deleted line
            try {
                int startOfLine = Utilities.getRowStart(messageArea, selectionStart);
                messageArea.setCaretPosition(startOfLine);
            } catch (BadLocationException e) {
                // If an exception occurs, set the caret position to the end of the text
                messageArea.setCaretPosition(messageArea.getText().length());
                e.printStackTrace();
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
