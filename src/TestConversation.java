import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import java.io.*;
/**
 * A program that tests Conversation class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
public class TestConversation {

    // Rule to create temporary folders and files for tests, ensuring tests do not affect production files.
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private User userOne;
    private User userTwo;
    private Message message;

    // Set up common objects needed for the tests before each test method is executed.
    @Before
    public void setUp() throws Exception {
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

    // Test to ensure the filename for the conversation is generated correctly.
    @Test
    public void conversationFilenameTest() {
        Conversation conversation = new Conversation(userOne, userTwo);
        Assert.assertEquals("The filename should be constructed correctly.", "Amy123_Bob456.txt",
                conversation.getFilename());
    }

    // Test adding a message to the conversation and verifying it is actually added.
    @Test
    public void addMessageTest() throws MessageSendErrorException, IOException {
        File tempFile = folder.newFile("Amy123_Bob456.txt");
        Conversation conversation = new Conversation(userOne, userTwo) {
            @ Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        conversation.addMessage(message);
        Assert.assertTrue("The message should be added to the conversation.", conversation.getMessages().contains(message));
    }

    // Test adding a message when the sender is blocked by the receiver, expecting an exception.
    @Test(expected = MessageSendErrorException.class)
    public void addMessageBlockedUserTest() throws MessageSendErrorException, IOException {
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
        conversation.addMessage(message);
    }

    // Test deleting a message from the conversation and verifying it is actually removed.
    @Test
    public void deleteMessageTest() throws MessageSendErrorException, IOException {
        File tempFile = folder.newFile("Amy123_Bob456.txt");
        Conversation conversation = new Conversation(userOne, userTwo) {
            @ Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        conversation.addMessage(message);
        conversation.deleteMessage(message);
        Assert.assertFalse("The message should be removed from the conversation.", conversation.getMessages().contains(message));
    }

    // Test attempting to delete a message without having permission, expecting an exception.
    @Test(expected = MessageSendErrorException.class)
    public void deleteMessageWithoutPermissionTest() throws MessageSendErrorException, IOException {
        File tempFile = folder.newFile("Amy123_Bob456.txt");
        Conversation conversation = new Conversation(userOne, userTwo) {
            @ Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        Message messageFromUserTwo = new Message("text", "Bob456", "Amy123", "Hello, Amy!");
        conversation.addMessage(messageFromUserTwo);
        conversation.deleteMessage(messageFromUserTwo);
    }

    // Test reading and writing messages to a file, ensuring that messages are correctly persisted.
    @Test
    public void readWriteMessagesTest() throws IOException {
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

        conversation.readMessages();
        Assert.assertFalse("The messages list should not be empty after reading from the file.", conversation.getMessages().isEmpty());
        Assert.assertEquals("The content of the message should match.", message.toString(), conversation.getMessages().get(0).toString());
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestConversation.class);
        if (result.wasSuccessful()) {
            System.out.println("All tests passed!");
        } else {
            result.getFailures().forEach(failure -> System.out.println(failure.toString()));
        }
    }
}
