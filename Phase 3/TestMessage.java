import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import java.util.Date;
/**
 * A program that tests Message class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
public class TestMessage {

    // Tests the constructor and getter methods of the Message class.
    @Test
    public void testConstructorAndGetterMethods() {
        String type = "text";
        String sender = "Amy123";
        String receiver = "Bob456";
        String content = "Hello, Bob!";
        Message message = new Message(type, sender, receiver, content);

        // Verify each component through its getter method
        Assert.assertEquals("Checking message type", type, message.getType());
        Assert.assertNotNull("Timestamp should not be null", message.getTimestamp());
        Assert.assertEquals("Checking message sender", sender, message.getSender());
        Assert.assertEquals("Checking message receiver", receiver, message.getReceiver());
        Assert.assertEquals("Checking message content", content, message.getContent());
    }

    // Tests the constructor that deserializes a message from a string.
    @Test
    public void testSerializationConstructor() {
        String type = "photo";
        String sender = "Amy123";
        String receiver = "Bob456";
        String content = "image.jpg";
        long currentTime = System.currentTimeMillis();
        String serializedData = type + "|" + currentTime + "|" + sender + "|" + receiver + "|" + content;
        Message message = new Message(serializedData);

        // Verify the deserialized message matches the original data
        Assert.assertEquals("Checking message type from serialized data", type, message.getType());
        Assert.assertEquals("Checking message sender from serialized data", sender, message.getSender());
        Assert.assertEquals("Checking message receiver from serialized data", receiver, message.getReceiver());
        Assert.assertEquals("Checking message content from serialized data", content, message.getContent());
        // The timestamp comparison might have a tiny difference due to the time it takes to run the code
        Assert.assertTrue("Checking message timestamp from serialized data",
                Math.abs(currentTime - message.getTimestamp().getTime()) < 1000);
    }

    // Tests the equals method for messages.
    @Test
    public void testEqualsMethod() {
        Message message1 = new Message("text", "Amy123", "Bob456", "Hi Bob!");
        Message message2 = new Message("text", "Amy123", "Bob456", "Hi Bob!");

        Assert.assertTrue("Two messages with the same content should be equal", message1.equals(message2));
    }

    // Tests the toString method for messages.
    @Test
    public void testToStringMethod() {
        String type = "text";
        String sender = "Amy123";
        String receiver = "Bob456";
        String content = "Hello, Bob!";
        Message message = new Message(type, sender, receiver, content);
        String expectedString = type + "|" + message.getTimestamp().getTime()
                + "|" + sender + "|" + receiver + "|" + content;

        String toStringOutput = message.toString();
        // Verify the toString output matches the expected format
        Assert.assertTrue("The toString method should contain the type",
                toStringOutput.startsWith(type + "|"));
        Assert.assertTrue("The toString method should contain the sender",
                toStringOutput.contains("|" + sender + "|"));
        Assert.assertTrue("The toString method should contain the receiver",
                toStringOutput.contains("|" + receiver + "|"));
        Assert.assertTrue("The toString method should end with the content",
                toStringOutput.endsWith("|" + content));
    }

    // Main method to run all the tests in this class and report the results.
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestMessage.class);
        if (result.wasSuccessful()) {
            System.out.println("All tests passed!");
        } else {
            result.getFailures().forEach(failure -> System.out.println(failure.toString()));
        }
    }
}
