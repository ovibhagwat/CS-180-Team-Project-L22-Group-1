import java.io.*;
import java.util.*;
/**
 * A program that implements DatabaseManage class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
public class DatabaseManage implements DatabaseManageInterface {

    // Filename where account information is stored.
    public static String accountFilename = "account.txt";
    private static final Object gatekeeper = new Object();

    //Creates a new user account with a unique account ID and password, and stores this information in the database.
    public static User createAccount(String accountID, String password)
            throws AccountErrorException, PasswordErrorException {
        try (BufferedReader bfr = new BufferedReader(new FileReader(accountFilename))) {
            String line = "";
            if (accountExist(accountID)) {
                throw new AccountErrorException("Account exists already.");
            }
            if (password.contains(" ")) {
                throw new PasswordErrorException("Password should not contain space.");
            }
            User u = new User(accountID, password, accountID + ".txt");
            synchronized (gatekeeper) {
                PrintWriter pw = new PrintWriter(new FileWriter(accountFilename, true));
                pw.println(u);
                pw.flush();
                pw.close();
            }
            return u;
        } catch (IOException e) {
            return null;
        }
    }

    //Attempts to log in a user with the provided account ID and password.
    public static User login(String accountID, String password) throws PasswordErrorException,
            AccountErrorException {
        try (BufferedReader bfr = new BufferedReader(new FileReader(accountFilename))) {
            String line = "";
            while (true) {
                synchronized (gatekeeper) {
                    line = bfr.readLine();
                }
                if (line == null) {
                    break;
                }
                String[] parts = line.split(" ");
                if (parts[0].equals(accountID)) {
                    if (parts[1].equals(password)) {
                        User u = new User(parts[0], parts[1], parts[2]);
                        u.readUser();
                        return u;
                    } else {
                        throw new PasswordErrorException("Password is not correct.");
                    }
                }
            }
            throw new AccountErrorException("Account does not exist, please create one.");


        } catch (IOException e) {
            return null;
        }
    }

    // Checks if an account with the specified account ID exists in the database.
    public static boolean accountExist(String accountID) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(accountFilename))) {
            String line = "";
            while (true) {
                synchronized (gatekeeper) {
                    line = bfr.readLine();
                }
                if (line == null) {
                    break;
                }
                if (line.substring(0, line.indexOf(" ")).equals(accountID)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    // Creates a conversation between two users and logs the initial message.
    public static Conversation createConversation(User sender, User receiver, Message message)
            throws MessageSendErrorException {
        if (receiver.isBlocked(sender.getAccountID())) {
            throw new MessageSendErrorException("You have been blocked by the receiver.");
        }
        Conversation c = new Conversation(sender, receiver);
        c.addMessage(message);
        c.writeMessages();
        sender.addConversationFilenames(c.getFilename());
        receiver.addConversationFilenames(c.getFilename());
        return c;
    }
}