import java.util.Date;
/**
 * A program that implements Message interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
public interface MessageInterface {

    // Getter methods for accessing the private fields.
    String getType();

    Date getTimestamp();

    String getContent();

    String getReceiver();

    String getSender();

    // compares this message with another object to determine equality. Messages are considered equal.
    // if all their fields match.
    boolean equals(Object o);

    // Provides a string representation of the message, suitable for storing or transmitting.
    // Format: type|timestamp|sender|receiver|content
    @Override
    String toString();
}
