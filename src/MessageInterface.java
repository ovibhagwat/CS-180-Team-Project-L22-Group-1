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
    String getType();

    Date getTimestamp();

    String getContent();

    String getReceiver();

    String getSender();

    boolean equals(Object o);

    @Override
    String toString();
}
