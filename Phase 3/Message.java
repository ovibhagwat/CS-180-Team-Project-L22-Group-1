import java.io.Serializable;
import java.util.Date;
/**
 * A program that implements Message class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu, Ovi Bhagwat
 * @version April 1, 2024
 */
public class Message implements MessageInterface, Serializable {
    // Specifies the type of the message: "text" or "photo".
    private String type; // either "text" or "photo"
    // Timestamp when the message was created.
    private Date timestamp;
    // Account ID of the sender.
    private String sender;
    // Account ID of the receiver.
    private String receiver;
    // The content of the message. For "text", it's the actual message; for "photo", it's the filename.
    private String content; // either content for "text" or filename for "photo"

    // Constructor for creating a new message with specified details.
    public Message(String type, String sender, String receiver, String content) {
        this.type = type;
        this.timestamp = new Date(); // Capture the current time as the timestamp.
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;

    }

    // Constructor that parses a string representation of a message to create a message object.
    // The string format is expected to be: type|timestamp|sender|receiver|content
    public Message(String data) {
        // Extract and assign values from the string representation.
        this.type = data.substring(0, data.indexOf("|"));
        data = data.substring(data.indexOf("|") + 1);
        this.timestamp = new Date(Long.parseLong(data.substring(0, data.indexOf("|"))));
        data = data.substring(data.indexOf("|") + 1);
        this.sender = data.substring(0, data.indexOf("|"));
        data = data.substring(data.indexOf("|") + 1);
        this.receiver = data.substring(0, data.indexOf("|"));
        this.content = data.substring(data.indexOf("|") + 1);
    }

    // Getter methods for accessing the private fields.
    public String getType() {
        return type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    // compares this message with another object to determine equality. Messages are considered equal.
    // if all their fields match.
    public boolean equals(Object o) {
        return o instanceof Message
                && ((Message) o).getType().equals(getType())
                && ((Message) o).getTimestamp().equals(getTimestamp())
                && ((Message) o).getSender().equals(getSender())
                && ((Message) o).getReceiver().equals(getReceiver())
                && ((Message) o).getContent().equals(getContent());
    }

    // Provides a string representation of the message, suitable for storing or transmitting.
    // Format: type|timestamp|sender|receiver|content
    @Override
    public String toString() {
        return type + "|" + timestamp.getTime() + "|" + sender + "|" + receiver + "|" + content;
    }
}