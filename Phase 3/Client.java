import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;
import javax.swing.filechooser.FileNameExtensionFilter;
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
    private JTextField loginAccountIDField;
    private JTextField accountIDField;
    private JTextField usernameField;
    private JTextField friendIDField;
    private JTextField blockIDField;
    private JTextField newUsernameField;
    private JTextArea newProfileArea;
    private JButton changeUsernameButton;
    private JButton changeProfileButton;
    private JPasswordField loginPasswordField;
    private JPasswordField passwordField;
    private JPasswordField passwordField2;
    private ArrayList<Message> messages = new ArrayList<>();

    private Conversation conversation;
    private JPanel chatPanel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a"); // Example format: "12:15 PM"
    private JTextField messageInputField;
    // Maintain a mapping between message components and their delete buttons for toggling visibility
    private Map<JComponent, JButton> deleteButtons = new HashMap<>();

    private JButton sendPhotoButton;

    private User friend;


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
        frame.setSize(600,600);

        // Using cardLayout
        cardLayout = new CardLayout();
        panels = new JPanel(cardLayout);

        addLoginPanel();
        addAccountCreationPanel();
        //addChangeUsernamePanel();
        //addChangePasswordPanel();
        //addChangeProfilePanel();

        frame.add(panels);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    // Method to create Login Panel of GUI
    public void addLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Login"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        loginAccountIDField = new JTextField(20);
        loginPasswordField = new JPasswordField(20);
        constraints.gridx = 0;
        constraints.gridy = 0;
        loginPanel.add(new JLabel("Username"), constraints);

        constraints.gridx = 1;
        loginPanel.add(loginAccountIDField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        loginPanel.add(new JLabel("Password"), constraints);
        constraints.gridx = 1;
        loginPanel.add(loginPasswordField, constraints);
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
        String username = loginAccountIDField.getText();
        char[] password = loginPasswordField.getPassword();

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
                //JFrame welcomeFrame = (JFrame) SwingUtilities.getWindowAncestor(panels);
                //welcomeFrame.dispose();
                addMainPanel();
                panels.revalidate();
                panels.repaint();
                cardLayout.show(panels, "MainPage");
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

        accountIDField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        passwordField2 = new JPasswordField(20);

        constraints.gridx = 0;
        constraints.gridy = 0;
        createPanel.add(new JLabel("Set AccountID"), constraints);
        constraints.gridx = 1;
        createPanel.add(accountIDField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        createPanel.add(new JLabel("Set Username"), constraints);
        constraints.gridx = 1;
        createPanel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        createPanel.add(new JLabel("Set Password"), constraints);
        constraints.gridx = 1;
        createPanel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        createPanel.add(new JLabel("Confirm Password"), constraints);
        constraints.gridx = 1;
        createPanel.add(passwordField2, constraints);

        JButton createButton = new JButton("Create Account");
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;

        createPanel.add(createButton, constraints);
        JButton switchToLoginButton = new JButton("Back to Login");
        constraints.gridy = 5;
        createPanel.add(switchToLoginButton, constraints);

        createButton.addActionListener(e -> handleCreateAccount());
        switchToLoginButton.addActionListener(e -> cardLayout.show(panels, "Login"));

        panels.add(createPanel, "CreateAccount");
    }

    // Method to handle Create Account request
    public void handleCreateAccount() {
        String accountID = accountIDField.getText();
        String loginUsername = usernameField.getText();
        char[] password = passwordField.getPassword();
        char[] password2 = passwordField2.getPassword();
        if (!Arrays.equals(password, password2)) {
            JOptionPane.showMessageDialog(null, "Passwords must match!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField2.setText("");
            return;
        } else if(password.length == 0 && password2.length == 0) {
            JOptionPane.showMessageDialog(null, "You must enter something for the password!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField2.setText("");
            return;
        }
        // Prepare request parameters
        Map<String, Object> params = new HashMap<>();
        params.put("accountID", accountID);
        params.put("password", new String(password));
        RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.CREATE_ACCOUNT, params);
        sendRequest(request);
        RequestResponseProtocol.Response response = receiveResponse();
        if (response != null) {
            if (response.getType() == RequestResponseProtocol.ResponseType.DATA) {
                user = (User) response.getData();
                user.changeUserName(loginUsername);
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

        changeUsernameButton.addActionListener(e -> handleChangeUsername());

        panels.add(panel, "ChangeUsername");

    }

    // Method to handle change username request
    public void handleChangeUsername() {
        String newUsername = newUsernameField.getText();
        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        params.put("newName", newUsername);
        RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.CHANGE_USER_NAME, params);
        sendRequest(request);
        RequestResponseProtocol.Response response = receiveResponse();

        if (response != null) {
            if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS) {
                JOptionPane.showMessageDialog(null, "Username successfully updated.",
                        "Change Username", JOptionPane.INFORMATION_MESSAGE);
                user.changeUserName(newUsername);
                panels.revalidate();
                panels.repaint();
                addMainPanel();
                cardLayout.show(panels, "MainPage");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update username: " + response.getErrorCode(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No response from server",
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
        panel.add(changeProfileButton, constraints);

        changeProfileButton.addActionListener(e -> handleChangeProfile());

        panels.add(panel, "ChangeProfile");
    }

    // Method to handle change profile request
    public void handleChangeProfile() {
        String newProfile = newProfileArea.getText();
        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        params.put("newProfile", newProfile);
        RequestResponseProtocol.Request request =
                new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.CHANGE_PROFILE, params);
        sendRequest(request);
        RequestResponseProtocol.Response response = receiveResponse();
        if (response != null) {
            if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS) {
                JOptionPane.showMessageDialog(null, "Profile updated successfully.",
                        "Profile Update", JOptionPane.INFORMATION_MESSAGE);
                user.changeUserProfile(newProfile);
                panels.revalidate();
                panels.repaint();
                addMainPanel();
                cardLayout.show(panels, "MainPage");
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

    public void addMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0x075E54));
        JLabel titleLabel = new JLabel("Welcome to Message180");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        // Username Label
        JLabel usernameLabel = new JLabel("Welcome Back, " + user.getUserName() + "!");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.BLACK); // Adjust color as needed

        // Account ID Label
        JLabel accountIDLabel = new JLabel("Account ID: " + user.getAccountID());
        accountIDLabel.setFont(new Font("Arial", Font.BOLD, 16));
        accountIDLabel.setForeground(Color.BLACK); // Adjust color as needed

        // Profile Text Area (Displays the user's profile)
        JLabel profileTextArea;
        if (user.getUserProfile() != null) {
            profileTextArea = new JLabel(user.getUserProfile());
            profileTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
            profileTextArea.setForeground(Color.GRAY); // Adjust color as needed
            profileTextArea.setBorder(null); // Remove border for a cleaner look
            profileTextArea.setBackground(Color.WHITE); // Background color matches the panel's background
        } else {
            profileTextArea = new JLabel("");
            profileTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
            profileTextArea.setForeground(Color.GRAY); // Adjust color as needed
            profileTextArea.setBorder(null); // Remove border for a cleaner look
            profileTextArea.setBackground(Color.WHITE); // Background color matches the panel's background
        }

        constraints.gridx = 0;
        constraints.gridy = 0;
        contentPanel.add(usernameLabel, constraints);

        constraints.gridy = 1;
        contentPanel.add(accountIDLabel, constraints);

        constraints.gridy = 2;
        constraints.gridheight = 3;
        contentPanel.add(new JScrollPane(profileTextArea), constraints);

        constraints.gridheight = 1;

        constraints.gridy = 6;
        JButton changeUsernameButton = new JButton("Change Username");
        changeUsernameButton.setBackground(new Color(0x075E54));
        changeUsernameButton.setForeground(Color.WHITE);
        contentPanel.add(changeUsernameButton, constraints);

        constraints.gridy = 7;
        JButton changeProfileButton = new JButton("Change Profile");
        changeProfileButton.setBackground(new Color(0x075E54));
        changeProfileButton.setForeground(Color.WHITE);
        contentPanel.add(changeProfileButton, constraints);

        constraints.gridy = 8;
        JButton startChatButton = new JButton("Start a chat!");
        startChatButton.setBackground(new Color(0x075E54));
        startChatButton.setForeground(Color.WHITE);
        contentPanel.add(startChatButton, constraints);

        constraints.gridy = 9;
        JButton modifyFriendButton = new JButton("Modify Friends");
        modifyFriendButton.setBackground(new Color(0x075E54));
        modifyFriendButton.setForeground(Color.WHITE);
        contentPanel.add(modifyFriendButton, constraints);

        constraints.gridy = 10;
        JButton modifyBlockButton = new JButton("Modify Block");
        modifyBlockButton.setBackground(new Color(0x075E54));
        modifyBlockButton.setForeground(Color.WHITE);
        contentPanel.add(modifyBlockButton, constraints);

        constraints.gridy = 11;
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(0x075E54));
        logoutButton.setForeground(Color.WHITE);
        contentPanel.add(logoutButton, constraints);

        logoutButton.addActionListener(e -> {
            JFrame welcomeFrame = (JFrame) SwingUtilities.getWindowAncestor(panels);
            welcomeFrame.dispose();
            createAndShowGUI();
        });

        changeUsernameButton.addActionListener(e -> {
            addChangeUsernamePanel();
            cardLayout.show(panels, "ChangeUsername");
        });

        changeProfileButton.addActionListener(e -> {
            addChangeProfilePanel();
            cardLayout.show(panels, "ChangeProfile");
        });
        startChatButton.addActionListener(e -> showChatPanel());
        modifyFriendButton.addActionListener(e -> showFriendPanel());
        modifyBlockButton.addActionListener(e -> showBlockPanel());

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        panels.add(mainPanel, "MainPage");
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
                Map<String, Object> params = new HashMap<>();
                params.put("user", user);
                params.put("friendAccountID", accountID);
                RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.ADD_FRIEND, params);
                sendRequest(request);
                RequestResponseProtocol.Response response = receiveResponse();
                if (response != null) {
                    if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                        JOptionPane.showMessageDialog(this, "Failed to add friend: " + response.getErrorCode(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS){
                        try {
                            user.addFriend(accountID);
                        } catch (FriendBlockErrorException ex) {
                            throw new RuntimeException(ex);
                        }
                        JOptionPane.showMessageDialog(null, "Add friend successfully!",
                                "Add Friend", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(panels, "MainPage");
                    }
                }
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
                Map<String, Object> params = new HashMap<>();
                params.put("user", user);
                params.put("friendAccountID", accountID);
                RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.REMOVE_FRIEND, params);
                sendRequest(request);
                RequestResponseProtocol.Response response = receiveResponse();
                if (response != null) {
                    if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                        JOptionPane.showMessageDialog(this, "Failed to remove friend: " + response.getErrorCode(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS){
                        try {
                            user.removeFriend(accountID);
                        } catch (FriendBlockErrorException ex) {
                            throw new RuntimeException(ex);
                        }
                        JOptionPane.showMessageDialog(null, "Remove friend successfully!",
                                "Remove Friend", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(panels, "MainPage");
                    }
                }
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
                Map<String, Object> params = new HashMap<>();
                params.put("user", user);
                params.put("blockAccountID", accountID);
                RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.BLOCK_USER, params);
                sendRequest(request);
                RequestResponseProtocol.Response response = receiveResponse();
                if (response != null) {
                    if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                        JOptionPane.showMessageDialog(this, "Failed to add block: " + response.getErrorCode(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS){
                        try {
                            user.addBlock(accountID);
                        } catch (FriendBlockErrorException ex) {
                            throw new RuntimeException(ex);
                        }
                        JOptionPane.showMessageDialog(null, "Add Block successfully!",
                                "Add Block", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(panels, "MainPage");

                    }
                }
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
                Map<String, Object> params = new HashMap<>();
                params.put("user", user);
                params.put("blockAccountID", accountID);
                RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.UNBLOCK_USER, params);
                sendRequest(request);
                RequestResponseProtocol.Response response = receiveResponse();
                if (response != null) {
                    if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                        JOptionPane.showMessageDialog(this, "Failed to remove block: " + response.getErrorCode(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (response.getType() == RequestResponseProtocol.ResponseType.SUCCESS) {
                        try {
                            user.removeBlock(accountID);
                        } catch (FriendBlockErrorException ex) {
                            throw new RuntimeException(ex);
                        }
                        JOptionPane.showMessageDialog(null, "Remove Block successfully!",
                                "Remove Block", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(panels, "MainPage");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please choose a user!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        returnButton.addActionListener(e -> cardLayout.show(panels, "Block"));
        panels.add(rBPanel, "rB");
        cardLayout.show(panels, "rB");
    }

    public String generateConversationFilename(String userOne, String userTwo) {
        String filename = "";
        if (userOne.compareTo(userTwo) < 0) {
            filename = userOne + "_" + userTwo + ".txt";
        } else if (userOne.compareTo(userTwo) > 0) {
            filename =  userTwo + "_" + userOne + ".txt";
        }
        return filename;
    }
    public void showChatPanel() {
        String friendID = (String) JOptionPane.showInputDialog(null, "Chooose a friend to start chat ", "Start Chat",
                JOptionPane.PLAIN_MESSAGE, null, user.getFriendsList().toArray(new String[0]), null);
        if (friendID == null) {
            return;
        }
        friend = new User(friendID);
        String filename = generateConversationFilename(user.getAccountID(), friendID);
        if (!user.getConversationFilenames().contains(filename)) {
            Map<String, Object> params = new HashMap<>();
            params.put("sender", user);
            params.put("receiver", friend);
            RequestResponseProtocol.Request request =
                    new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.CREATE_CONVERSATION, params);
            sendRequest(request);
            RequestResponseProtocol.Response response = receiveResponse();
            if (response != null) {
                if (response.getType() == RequestResponseProtocol.ResponseType.DATA) {
                    conversation = (Conversation) response.getData();
                    messages = new ArrayList<>();
                    JOptionPane.showMessageDialog(this, "Start Chat successful!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                    JOptionPane.showMessageDialog(this, "Start chat failed: " + response.getErrorCode(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null, "No response from server.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;

            }
        } else {
            conversation = new Conversation(user, friend);
            Map<String, Object> params = new HashMap<>();
            params.put("conversation", conversation);
            RequestResponseProtocol.Request request =
                    new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.FETCH_MESSAGES, params);
            sendRequest(request);
            RequestResponseProtocol.Response response = receiveResponse();
            if (response != null) {
                if (response.getType() == RequestResponseProtocol.ResponseType.DATA) {
                    conversation = (Conversation) response.getData();
                    messages = conversation.getMessages();
                    JOptionPane.showMessageDialog(this, "Start Chat successful!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                    JOptionPane.showMessageDialog(this, "Start chat failed: " + response.getErrorCode(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null, "No response from server.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        JFrame chatJF = new JFrame("Chat Window");
        chatJF.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(chatPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);


        for (Message message : messages) {
            if (message instanceof PhotoMessage) {
                addPhotoMessage((PhotoMessage) message);
            } else {
                addMessage(message);
            }
        }

        // Message input field and send button
        messageInputField = new JTextField(30);
        // Add ActionListener to the message input field
        messageInputField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageInputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatJF.add(scrollPane, BorderLayout.CENTER);
        chatJF.add(inputPanel, BorderLayout.SOUTH);


        // Load the icon image
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("icon.png"));
        // Scale it to fit the UI
        Image scaledImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH); // Set the width and height as needed
        ImageIcon icon = new ImageIcon(scaledImage);

        // Use the scaled icon for the button
        sendPhotoButton = new JButton(icon);

        sendPhotoButton.addActionListener(e -> sendPhotoMessage());

        // Modify the input panel to include the send photo button
        inputPanel.add(sendPhotoButton, BorderLayout.WEST);

        chatJF.setSize(400, 600);
        chatJF.setVisible(true);

        scrollToBottom(scrollPane);
    }

    private void addMessage(Message message) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout(message.getSender().equals(user.getAccountID()) ? FlowLayout.RIGHT : FlowLayout.LEFT));
        messagePanel.setOpaque(false);

        JLabel messageLabel = new JLabel(makeHtmlMessage(message));
        messageLabel.setOpaque(true);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messageLabel.setBackground(message.getSender().equals(user.getAccountID()) ? new Color(173, 216, 230) : new Color(255, 228,
                225));

        messagePanel.add(messageLabel);
        chatPanel.add(messagePanel);

        // Add delete button if the sender is "You"
        if (message.getSender().equals(user.getAccountID())) {
            JButton deleteButton = new JButton("Delete it");
            deleteButton.setVisible(false); // Start invisible
            deleteButton.addActionListener(e -> deleteMessage(messagePanel, message));
            deleteButtons.put(messagePanel, deleteButton); // Map the message panel to the delete button

            messageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        // Show the delete button
                        toggleDeleteButton(messagePanel);
                    }
                }
            });
        }

        chatPanel.revalidate();
        chatPanel.repaint();
    }

    private void toggleDeleteButton(JPanel messagePanel) {
        JButton deleteButton = deleteButtons.get(messagePanel);
        if (deleteButton != null) {
            boolean isVisible = deleteButton.isVisible();
            deleteButton.setVisible(!isVisible);
            if (!isVisible) {
                messagePanel.add(deleteButton);
            } else {
                messagePanel.remove(deleteButton);
            }
            messagePanel.revalidate();
            messagePanel.repaint();
        }
    }

    private String makeHtmlMessage(Message message) {
        String formattedTime = dateFormat.format(message.getTimestamp());
        if (message.getSender().equals(user.getAccountID())) {
            return String.format("<html><div style='width: 200px;'>%s %s<br/>%s</div></html>",
                    user.getUserName(), formattedTime, message.getContent());
        } else {
            return String.format("<html><div style='width: 200px;'>%s %s<br/>%s</div></html>",
                    friend.getUserName(), formattedTime, message.getContent());
        }

    }

    private void sendMessage() {
        String content = messageInputField.getText().trim();
        if (!content.isEmpty()) {
            Message message = new Message("text",  user.getAccountID(), friend.getAccountID(), content);
            Map<String, Object> params = new HashMap<>();
            params.put("conversation", conversation);
            params.put("message", message);
            RequestResponseProtocol.Request request =
                    new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.SEND_MESSAGE, params);
            sendRequest(request);
            RequestResponseProtocol.Response response = receiveResponse();
            if (response != null) {
                if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                    JOptionPane.showMessageDialog(this, "Send message failed: " + response.getErrorCode(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No response from server.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            messages.add(message);
            addMessage(message);
            messageInputField.setText("");
            // Make sure the new message is shown
            chatPanel.revalidate();
            chatPanel.repaint();
            // Scroll to the new message
            scrollToBottom((JScrollPane) chatPanel.getParent().getParent());
        }
    }

    private void sendPhotoMessage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(file);

                PhotoMessage photoMessage = new PhotoMessage("Photo", user.getAccountID(), friend.getAccountID(), "",
                        file.getAbsolutePath());

                Map<String, Object> params = new HashMap<>();
                params.put("conversation", conversation);
                params.put("photoMessage",photoMessage);
                RequestResponseProtocol.Request request =
                        new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.SEND_PHOTO_MESSAGE,
                                params);
                sendRequest(request);
                RequestResponseProtocol.Response response = receiveResponse();
                if (response != null) {
                    if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                        JOptionPane.showMessageDialog(this, "Send message failed: " + response.getErrorCode(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No response from server.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                addPhotoMessage(photoMessage);
            } catch (IOException ex) {
                ex.printStackTrace();
                // Handle error - show a dialog, etc.
            }
        }
    }

    private void addPhotoMessage(PhotoMessage photoMessage) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout(photoMessage.getSender().equals(user.getAccountID()) ? FlowLayout.RIGHT :
                FlowLayout.LEFT));
        messagePanel.setOpaque(false);

        JLabel photoLabel = new JLabel(new ImageIcon(photoMessage.getImage().getScaledInstance(300, -1,
                Image.SCALE_SMOOTH)));
        messagePanel.add(photoLabel);

        JButton deleteButton = new JButton("Delete it");
        deleteButton.setVisible(false);
        deleteButton.addActionListener(e -> deleteMessage(messagePanel, photoMessage));
        deleteButtons.put(messagePanel, deleteButton);

        photoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    // Show the delete button
                    toggleDeleteButton(messagePanel);
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    // Double-click to show original size photo
                    showOriginalSizePhoto(photoMessage);
                }
            }
        });

        messagePanel.add(photoLabel);
        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom((JScrollPane) chatPanel.getParent().getParent());
    }

    private void showOriginalSizePhoto(PhotoMessage photoMessage) {
        BufferedImage originalImage = photoMessage.getImage();

        // Create a JLabel to display the original size photo
        JLabel originalPhotoLabel = new JLabel(new ImageIcon(originalImage));

        // Create a JScrollPane to allow scrolling if the photo is too large for the dialog
        JScrollPane scrollPane = new JScrollPane(originalPhotoLabel);
        scrollPane.setPreferredSize(new Dimension(originalImage.getWidth(), originalImage.getHeight()));

        // Create a JDialog to show the original size photo
        JDialog dialog = new JDialog();
        dialog.setTitle("Original Size Photo");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(scrollPane);
        dialog.pack();
        dialog.setLocationRelativeTo(this); // Center the dialog relative to the main window
        dialog.setVisible(true);
    }


    private void scrollToBottom(JScrollPane scrollPane) {
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
    }

    private void deleteMessage(JPanel messagePanel, Message message) {
        Map<String, Object> params = new HashMap<>();
        params.put("conversation", conversation);
        params.put("message", message);
        RequestResponseProtocol.Request request =
                new RequestResponseProtocol.Request(RequestResponseProtocol.RequestType.DELETE_MESSAGE, params);
        sendRequest(request);
        RequestResponseProtocol.Response response = receiveResponse();
        if (response != null) {
            if (response.getType() == RequestResponseProtocol.ResponseType.ERROR) {
                JOptionPane.showMessageDialog(this, "Delete message failed: " + response.getErrorCode(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No response from server.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        messages.remove(message);
        deleteButtons.remove(messagePanel);
        chatPanel.remove(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();
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