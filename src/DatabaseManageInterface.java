import java.io.*;
/**
 * A program that implements DatabaseManage interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
public interface DatabaseManageInterface {
    static User createAccount(String accountID, String password)
            throws AccountErrorException, PasswordErrorException {
        try (BufferedReader bfr = new BufferedReader(new FileReader(DatabaseManage.accountFilename))){
            String line = "";
            if (accountExist(accountID)) {
                throw new AccountErrorException("Account exists already.");
            }
            if (password.contains(" ")) {
                throw new PasswordErrorException("Password should not contain space.");
            }
            User u = new User(accountID, password, accountID + ".txt");
            PrintWriter pw = new PrintWriter(new FileWriter(DatabaseManage.accountFilename, true));
            pw.println(u);
            pw.flush();
            pw.close();
            return u;
        } catch (IOException e) {
            return null;
        }
    }
    static User login(String accountID, String password) throws PasswordErrorException,
            AccountErrorException {
        try (BufferedReader bfr = new BufferedReader(new FileReader(DatabaseManage.accountFilename))) {
            String line = "";
            while ((line = bfr.readLine()) != null) {
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

    static boolean accountExist(String accountID) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(DatabaseManage.accountFilename))) {
            String line = "";
            while ((line = bfr.readLine()) != null) {
                if (line.substring(0, line.indexOf(" ")).equals(accountID)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

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
