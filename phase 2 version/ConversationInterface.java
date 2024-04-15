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
    // Returns the filename where the conversation is stored.
    String getFilename();
    // Returns the first user in the conversation.
    User getUserOne();
    // Returns the second user in the conversation.
    User getUserTwo();
    // Returns the list of messages in the conversation.
    ArrayList<Message> getMessages();
    // Adds a message to the conversation, checking if the sender is blocked.
    void addMessage(Message message) throws MessageSendErrorException;
    // Deletes a message from the conversation, if the requester has permission.
    void deleteMessage(Message message) throws MessageSendErrorException;
    // Reads messages from the conversation's file, loading them into the message list.
    void readMessages();
    // Writes the conversation's messages to its file, saving them.
    void writeMessages();
}
