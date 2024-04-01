import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import java.io.*;
/**
 * A program that tests DatabaseManage class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
public class TestDatabaseManage {

    // Rule to create a temporary folder that will be used to store test files, ensuring tests do not interfere with actual data.
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private User sender;
    private User receiver;
    private Message message;

    // Sets up common objects for the tests before each test method is executed.
    @Before
    public void setUp() throws Exception {
        // Redirect `accountFilename` to a temporary file
        File accountFile = folder.newFile("account.txt");
        File senderFile = folder.newFile("Amy123.txt");
        File receiverFile = folder.newFile("Bob456.txt");
        DatabaseManage.accountFilename = accountFile.getAbsolutePath();

        // Initialize User objects with paths pointing to temporary files.
        sender = new User("Amy123", "pass1", "Amy123.txt") {
            @Override
            public String getFilename() {
                return senderFile.getAbsolutePath();
            }
        };
        receiver = new User("Bob456", "pass2", "Bob456.txt") {
            @Override
            public String getFilename() {
                return receiverFile.getAbsolutePath();
            }
        };
        message = new Message("text", "Amy123", "Bob456", "Hello!");

    }

    // Test creating a new account successfully.
    @Test
    public void testCreateAccount() throws AccountErrorException, PasswordErrorException {
        User user = DatabaseManage.createAccount("testUser", "password123");
        Assert.assertNotNull("User should be created", user);
    }

    // Test attempting to create an account that already exists, which should throw an exception.
    @Test(expected = AccountErrorException.class)
    public void testCreateExistingAccount() throws AccountErrorException, PasswordErrorException {
        DatabaseManage.createAccount("existingUser", "password");
        // This should throw an exception because the account already exists
        DatabaseManage.createAccount("existingUser", "password");
    }

    // Test creating an account with a password that contains spaces, which should throw an exception.
    @Test(expected = PasswordErrorException.class)
    public void testCreateAccountWithSpaceInPassword() throws AccountErrorException, PasswordErrorException {
        // This should throw an exception because the password contains spaces
        DatabaseManage.createAccount("userWithSpace", "bad password");
    }

    // Test successful login.
    @Test
    public void testLoginSuccess() throws AccountErrorException, PasswordErrorException {
        User loginUser = DatabaseManage.createAccount("loginUser", "loginPass");
        if (loginUser != null) {
            loginUser.writeUser();
        }
        User loggedInUser = DatabaseManage.login("loginUser", "loginPass");
        Assert.assertNotNull("User should successfully log in", loggedInUser);
    }

    // Test login with incorrect password, expecting an exception.
    @Test(expected = PasswordErrorException.class)
    public void testLoginIncorrectPassword() throws AccountErrorException, PasswordErrorException {
        DatabaseManage.createAccount("userIncorrectPass", "password");
        // Attempt to login with an incorrect password
        DatabaseManage.login("userIncorrectPass", "wrongPassword");
    }

    // Test login with a non-existent account, expecting an exception.
    @Test(expected = AccountErrorException.class)
    public void testLoginNonExistentAccount() throws AccountErrorException, PasswordErrorException {
        // Attempt to login with a non-existent account
        DatabaseManage.login("nonExistentUser", "password");
    }

    // Test creating a conversation successfully, verifying it contains the sent message and is listed for both users.
    @Test
    public void testCreateConversationSuccess() throws MessageSendErrorException {
        Conversation conversation = DatabaseManage.createConversation(sender, receiver, message);
        Assert.assertNotNull("Conversation should be successfully created", conversation);
        Assert.assertTrue("Message should be added to the conversation", conversation.getMessages().contains(message));
        Assert.assertTrue("Sender should have the conversation in their list", sender.getConversationFilenames().contains(conversation.getFilename()));
        Assert.assertTrue("Receiver should have the conversation in their list", receiver.getConversationFilenames().contains(conversation.getFilename()));
    }

    // Test creating a conversation when the sender is blocked by the receiver, expecting an exception.
    @Test(expected = MessageSendErrorException.class)
    public void testCreateConversationBlockedSender() throws MessageSendErrorException {
        // Simulate receiver blocking the sender
        try {
            receiver.addBlock(sender.getAccountID());
        } catch (FriendBlockErrorException e) {
            e.printStackTrace();
        }

        // Attempt to create a conversation which should fail
        DatabaseManage.createConversation(sender, receiver, message);
    }

    // Entry point to run the test suite and print results.
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestDatabaseManage.class);
        if (result.wasSuccessful()) {
            System.out.println("All tests passed!");
        } else {
            result.getFailures().forEach(failure -> System.out.println(failure.toString()));
        }
    }
}
