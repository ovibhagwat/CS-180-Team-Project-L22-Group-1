import java.io.*;
import java.util.ArrayList;
/**
 * A program that implements Conversation class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu, Ovi Bhagwat
 * @version April 1, 2024
 */
public class Conversation implements ConversationInterface {
    // Filename for storing conversation messages, determined by user account IDs.
    private String filename;
    // The first user in the conversation.
    private User userOne;
    // The second user in the conversation.
    private User userTwo;
    // List of messages in the conversation.
    private ArrayList<Message> messages;

    private static final Object gatekeeper = new Object();

    // Constructor for creating a new conversation between two users.
    public Conversation(User userOne, User userTwo) {
        this.userOne = userOne;
        this.userTwo = userTwo;
        // Filename is generated based on the comparison of account IDs to ensure consistency.
        if (userOne.getAccountID().compareTo(userTwo.getAccountID()) < 0) {
            this.filename = userOne.getAccountID() + "_" + userTwo.getAccountID() + ".txt";
        } else if (userOne.getAccountID().compareTo(userTwo.getAccountID()) > 0) {
            this.filename =  userOne.getAccountID() + "_" + userTwo.getAccountID() + ".txt";
        } else {
            // An empty filename indicates an error or same user, which should not happen.
            this.filename = "";
        }
        this.messages = new ArrayList<>();
    }

    // Returns the filename where the conversation is stored.
    public String getFilename() {
        return filename;
    }

    // Returns the first user in the conversation.
    public User getUserOne() {
        return userOne;
    }

    // Returns the second user in the conversation.
    public User getUserTwo() {
        return userTwo;
    }

    // Returns the list of messages in the conversation.
    public ArrayList<Message> getMessages() {
        return messages;
    }

    // Adds a message to the conversation, checking if the sender is blocked.
    public void addMessage(Message message) throws MessageSendErrorException {
        if (getUserTwo().isBlocked(getUserOne().getAccountID())) {
            throw new MessageSendErrorException("You have been blocked by " + getUserTwo().getUserName() + ".");
        }
        getMessages().add(message);
        writeMessages();
    }

    // Deletes a message from the conversation, if the requester has permission.
    public void deleteMessage(Message message) throws MessageSendErrorException {
        if (!message.getSender().equals(getUserOne().getAccountID())) {
            throw new MessageSendErrorException("you don't have permission to delete it.");
        }
        getMessages().remove(message);
        writeMessages();
    }

    // Reads messages from the conversation's file, loading them into the message list.
    public void readMessages() {
        try (BufferedReader bfr = new BufferedReader(new FileReader(getFilename()))) {
            ArrayList<Message> myMessages = new ArrayList<>();
            String line = "";
            while (true) {
                synchronized (gatekeeper) {
                    line = bfr.readLine();
                }
                if (line == null) {
                    break;
                }
                myMessages.add(new Message(line));
            }
            this.messages = myMessages;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Writes the conversation's messages to its file, saving them.
    public void writeMessages() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getFilename()))) {
            for (Message m: getMessages()) {
                synchronized (gatekeeper) {
                    pw.println(m);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception if file writing fails.
        }
    }

}
