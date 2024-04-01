import java.util.ArrayList;
/**
 * A program that implements Conversation interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
public interface ConversationInterface {
    String getFilename();
    User getUserOne();
    User getUserTwo();
    ArrayList<Message> getMessages();
    void addMessage(Message message) throws MessageSendErrorException;
    void deleteMessage(Message message) throws MessageSendErrorException;
    void readMessages();
    void writeMessages();
}
