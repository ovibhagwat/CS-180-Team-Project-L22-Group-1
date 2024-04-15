import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * A program that implements TestClientHandler class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 15, 2024
 */

public class TestClientHandler {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ClientHandler clientHandler;
    private User userOne;
    private User userTwo;
    private Message message;


    @Before
    public void setUp() throws Exception {
        clientHandler = new ClientHandler(new Socket(), 1);
        // Redirect `accountFilename` to a temporary file
        File accountFile = folder.newFile("account.txt");
        DatabaseManage.accountFilename = accountFile.getAbsolutePath();
        File tempFile = folder.newFile("Bob456.txt");
        userOne = new User("Amy123", "pass1", "Amy123.txt");
        userTwo = new User("Bob456", "pass2", "Bob456.txt") {
            @ Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        message = new Message("text", "Amy123", "Bob456", "Hello, Bob!");

    }

    // Test creating a new account successfully.
    @Test
    public void testProcessCreateAccountRequestSuccess() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountID", "testUser");
        parameters.put("password", "password123");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.CREATE_ACCOUNT, parameters);
        RequestResponseProtocol.Response response = clientHandler.processCreateAccountRequest(request);

        Assert.assertEquals("Response should be DATA",
                RequestResponseProtocol.ResponseType.DATA, response.getType());
        Assert.assertTrue("Data should be User", response.getData() instanceof User);
    }

    // Test attempting to create an account that already exists, which should throw an exception.
    @Test
    public void testProcessCreateExistingAccountRequest() throws PasswordErrorException, AccountErrorException {
        DatabaseManage.createAccount("existingUser", "password");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountID", "existingUser");
        parameters.put("password", "password");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.
                RequestType.CREATE_ACCOUNT, parameters);
        RequestResponseProtocol.Response response = clientHandler.processCreateAccountRequest(request);

        Assert.assertEquals("Response should be ERROR",
                RequestResponseProtocol.ResponseType.ERROR, response.getType());
        Assert.assertEquals("Error code should be USER_ALREADY_EXISTS",
                RequestResponseProtocol.ErrorCode.USER_ALREADY_EXISTS, response.getErrorCode());

    }

    // Test creating an account with a password that contains spaces, which should throw an exception.
    @Test
    public void testProcessCreateAccountWithInvalidPasswordRequest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountID", "userWithSpace");
        parameters.put("password", "bad password");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.Request(RequestResponseProtocol.
                RequestType.CREATE_ACCOUNT, parameters);
        RequestResponseProtocol.Response response = clientHandler.processCreateAccountRequest(request);

        Assert.assertEquals("Response should be ERROR",
                RequestResponseProtocol.ResponseType.ERROR, response.getType());
        Assert.assertEquals("Error code should be INVALID_PASSWORD",
                RequestResponseProtocol.ErrorCode.INVALID_PASSWORD, response.getErrorCode());
    }

    // Test successful login.
    @Test
    public void testProcessLoginRequestSuccess() throws AccountErrorException, PasswordErrorException, IOException {
        User loginUser = DatabaseManage.createAccount("loginUser", "loginPass");
        if (loginUser != null) {
            loginUser.writeUser();
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountID", "loginUser");
        parameters.put("password", "loginPass");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.LOGIN, parameters);
        RequestResponseProtocol.Response response = clientHandler.processLoginRequest(request);
        Assert.assertEquals("Response should be DATA",
                RequestResponseProtocol.ResponseType.DATA, response.getType());
        Assert.assertTrue("Data should be User", response.getData() instanceof User);
    }

    // Tests error handling when the password is incorrect.
    @Test
    public void testProcessLoginIncorrectPasswordRequest() throws AccountErrorException, PasswordErrorException {
        DatabaseManage.createAccount("userIncorrectPass", "password");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountID", "userIncorrectPass");
        parameters.put("password", "wrongPassword");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.LOGIN, parameters);
        RequestResponseProtocol.Response response = clientHandler.processLoginRequest(request);
        Assert.assertEquals("Response should be ERROR",
                RequestResponseProtocol.ResponseType.ERROR, response.getType());
        Assert.assertEquals("Error code should be INCORRECT_PASSWORD",
                RequestResponseProtocol.ErrorCode.INCORRECT_PASSWORD, response.getErrorCode());
    }

    // Tests error handling when the user to login does not exist.
    @Test
    public void testLoginNonExistentAccount() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountID", "nonExistentUser");
        parameters.put("password", "password");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.LOGIN, parameters);
        RequestResponseProtocol.Response response = clientHandler.processLoginRequest(request);
        Assert.assertEquals("Response should be ERROR",
                RequestResponseProtocol.ResponseType.ERROR, response.getType());
        Assert.assertEquals("Error code should be USER_NOT_FOUND",
                RequestResponseProtocol.ErrorCode.USER_NOT_FOUND, response.getErrorCode());
    }

    // Tests change username success.
    @Test
    public void testProcessChangeUserNameRequest() throws IOException {
        File tempFile = folder.newFile("Amy135.txt");
        User user = new User("Amy135", "password", tempFile.getAbsolutePath());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("newName", "NewName");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.CHANGE_USER_NAME, parameters);
        RequestResponseProtocol.Response response = clientHandler.processChangeUserNameRequest(request);
        Assert.assertEquals("Response should be SUCCESS",
                RequestResponseProtocol.ResponseType.SUCCESS, response.getType());
        Assert.assertEquals("Username should be updated", "NewName", user.getUserName());
    }

    // Tests error handling when changing password.
    @Test
    public void testProcessChangePasswordErrorRequest() throws IOException {
        File tempFile = folder.newFile("Amy135.txt");
        User user = new User("Amy135", "password", tempFile.getAbsolutePath());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("newPassword", "password");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.CHANGE_PASSWORD, parameters);
        RequestResponseProtocol.Response response = clientHandler.processChangePasswordRequest(request);
        Assert.assertEquals("Response should be ERROR",
                RequestResponseProtocol.ResponseType.ERROR, response.getType());
        Assert.assertEquals("Error code should be SAME_PASSWORD",
                RequestResponseProtocol.ErrorCode.SAME_PASSWORD, response.getErrorCode());
    }

    // Tests change profile success.
    @Test
    public void testProcessChangeProfileRequest() throws IOException {
        File tempFile = folder.newFile("Amy135.txt");
        User user = new User("Amy135", "password", tempFile.getAbsolutePath());
        user.setUserProfile("I am Amy");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("newProfile", "NewProfile");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.CHANGE_USER_NAME, parameters);
        RequestResponseProtocol.Response response = clientHandler.processChangeProfileRequest(request);
        Assert.assertEquals("Response should be SUCCESS",
                RequestResponseProtocol.ResponseType.SUCCESS, response.getType());
        Assert.assertEquals("Profile should be updated", "NewProfile", user.getUserProfile());
    }

    // Tests add and remove friend success.
    @Test
    public void testProcessAddAndRemoveFriendRequest() throws IOException,
            PasswordErrorException, AccountErrorException {
        File tempFile = folder.newFile("Amy135.txt");
        DatabaseManage.createAccount("Bob246", "password");
        User user = new User("Amy135", "password", tempFile.getAbsolutePath());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("friendAccountID", "Bob246");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.ADD_FRIEND, parameters);
        RequestResponseProtocol.Response response = clientHandler.processAddFriendRequest(request);
        Assert.assertEquals("Response should be SUCCESS",
                RequestResponseProtocol.ResponseType.SUCCESS, response.getType());
        Assert.assertTrue("Friend list should contain 'Bob246'", user.getFriendsList().contains("Bob246"));
        RequestResponseProtocol.Request request2 = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.REMOVE_FRIEND, parameters);
        RequestResponseProtocol.Response response2 = clientHandler.processRemoveFriendRequest(request2);
        Assert.assertEquals("Response should be SUCCESS",
                RequestResponseProtocol.ResponseType.SUCCESS, response2.getType());
        Assert.assertFalse("Friend list should not contain 'Bob246'",
                user.getFriendsList().contains("Bob246"));
    }

    // Tests error handling when adding a block that already exists or removing a non-block.
    @Test
    public void testProcessAddAndRemoveBlockErrorTest() throws FriendBlockErrorException, IOException,
            PasswordErrorException, AccountErrorException {
        File tempFile = folder.newFile("Amy135.txt");
        DatabaseManage.createAccount("Bob246", "password");
        DatabaseManage.createAccount("Cathy135", "password");
        User user = new User("Amy135", "password", tempFile.getAbsolutePath());
        user.addBlock("Bob246");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("blockAccountID", "Bob246");
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.ADD_FRIEND, parameters);
        RequestResponseProtocol.Response response = clientHandler.processBlockRequest(request);
        Assert.assertEquals("Response should be ERROR",
                RequestResponseProtocol.ResponseType.ERROR, response.getType());
        Assert.assertEquals("Error code should be USER_BLOCKED",
                RequestResponseProtocol.ErrorCode.USER_BLOCKED, response.getErrorCode());
        Map<String, Object> parameters2 = new HashMap<>();
        parameters2.put("user", user);
        parameters2.put("blockAccountID", "Cathy135");
        RequestResponseProtocol.Request request2 = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.REMOVE_FRIEND, parameters2);
        RequestResponseProtocol.Response response2 = clientHandler.processUnblockRequest(request2);
        Assert.assertEquals("Response should be ERROR",
                RequestResponseProtocol.ResponseType.ERROR, response2.getType());
        Assert.assertEquals("Error code should be USER_NOT_BLOCKED",
                RequestResponseProtocol.ErrorCode.USER_NOT_BLOCKED, response2.getErrorCode());
    }

    // Tests add and delete message success.
    @Test
    public void testProcessAddMessageRequest() throws IOException {
        File tempFile = folder.newFile("Amy123_Bob456.txt");
        Conversation conversation = new Conversation(userOne, userTwo) {
            @ Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("conversation", conversation);
        parameters.put("message", message);
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.SEND_MESSAGE, parameters);
        RequestResponseProtocol.Response response = clientHandler.processSendMessageRequest(request);
        Assert.assertEquals("Response should be SUCCESS",
                RequestResponseProtocol.ResponseType.SUCCESS, response.getType());
        Assert.assertTrue("The message should be added to the conversation.",
                conversation.getMessages().contains(message));
    }

    // Tests error handling when adding a message that the receiver block the sender.
    @Test
    public void testAddMessageBlockedUserRequest() throws IOException {
        File tempFile = folder.newFile("Amy123_Bob456.txt");
        Conversation conversation = new Conversation(userOne, userTwo) {
            @ Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        try {
            userTwo.addBlock(userOne.getAccountID());
        } catch (FriendBlockErrorException e) {
            e.printStackTrace();
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("conversation", conversation);
        parameters.put("message", message);
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.SEND_MESSAGE, parameters);
        RequestResponseProtocol.Response response = clientHandler.processSendMessageRequest(request);
        Assert.assertEquals("Response should be ERROR",
                RequestResponseProtocol.ResponseType.ERROR, response.getType());
        Assert.assertEquals("Error code should be USER_BLOCKED",
                RequestResponseProtocol.ErrorCode.USER_BLOCKED, response.getErrorCode());
    }

    // Tests delete message success.
    @Test
    public void testDeleteMessageRequest() throws MessageSendErrorException, IOException {
        File tempFile = folder.newFile("Amy123_Bob456.txt");
        Conversation conversation = new Conversation(userOne, userTwo) {
            @ Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        conversation.addMessage(message);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("conversation", conversation);
        parameters.put("message", message);
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.DELETE_MESSAGE, parameters);
        RequestResponseProtocol.Response response = clientHandler.processDeleteMessageRequest(request);
        Assert.assertEquals("Response should be SUCCESS",
                RequestResponseProtocol.ResponseType.SUCCESS, response.getType());
        Assert.assertFalse("The message should be removed from the conversation.",
                conversation.getMessages().contains(message));
    }

    // Tests error handling when deleting a message but without permission.
    @Test
    public void testDeleteMessageWithoutPermissionRequest() throws MessageSendErrorException, IOException {
        File tempFile = folder.newFile("Amy123_Bob456.txt");
        Conversation conversation = new Conversation(userOne, userTwo) {
            @ Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        Message messageFromUserTwo = new Message("text", "Bob456",
                "Amy123", "Hello, Amy!");
        conversation.addMessage(messageFromUserTwo);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("conversation", conversation);
        parameters.put("message", messageFromUserTwo);
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.DELETE_MESSAGE, parameters);
        RequestResponseProtocol.Response response = clientHandler.processDeleteMessageRequest(request);
        Assert.assertEquals("Response should be ERROR",
                RequestResponseProtocol.ResponseType.ERROR, response.getType());
        Assert.assertEquals("Error code should be PERMISSION_DENIED",
                RequestResponseProtocol.ErrorCode.PERMISSION_DENIED, response.getErrorCode());
    }

    // Tests fetch message success.
    @Test
    public void testProcessFetchMessagesRequest() throws IOException {
        File tempFile = folder.newFile("Amy123_Bob456.txt");
        Conversation conversation = new Conversation(userOne, userTwo) {
            @ Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        try {
            conversation.addMessage(message);
        } catch (MessageSendErrorException e) {
            e.printStackTrace();
        }
        conversation.writeMessages();

        conversation = new Conversation(userOne, userTwo) {
            @ Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("conversation", conversation);
        conversation.readMessages();
        RequestResponseProtocol.Request request = new RequestResponseProtocol.
                Request(RequestResponseProtocol.RequestType.FETCH_MESSAGES, parameters);
        RequestResponseProtocol.Response response = clientHandler.processFetchMessagesRequest(request);
        Assert.assertEquals("Response should be DATA",
                RequestResponseProtocol.ResponseType.DATA, response.getType());
        Assert.assertTrue("The data is a object of Conversation",
                response.getData() instanceof Conversation);
        Assert.assertFalse("The messages list should not be empty after reading from the file.",
                conversation.getMessages().isEmpty());
        Assert.assertEquals("The content of the message should match.", message.toString(),
                conversation.getMessages().getFirst().toString());
    }

    // Entry point to run the test suite and print results.
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestClientHandler.class);
        if (result.wasSuccessful()) {
            System.out.println("All tests passed!");
        } else {
            result.getFailures().forEach(failure -> System.out.println(failure.toString()));
        }
    }
}
