import java.io.*;
import java.util.*;

public class DatabaseManage {
    private static String accountFilename = "account.txt";
    public static User createAccount(String accountID, String password)
            throws AccountExistException, PasswordInvalidException {
        try (BufferedReader bfr = new BufferedReader(new FileReader(accountFilename))){
            String line = "";
            if (accountExist(accountID)) {
                throw new AccountExistException("Account exists already.");
            }
            if (password.contains(" ")) {
                throw new PasswordInvalidException("Password should not contain space.");
            }
            User u = new User(accountID, password, accountID + ".txt");
            PrintWriter pw = new PrintWriter(new FileWriter(accountFilename, true));
            pw.println(u);
            pw.flush();
            pw.close();
            return u;
        } catch (IOException e) {
            return null;
        }
    }
    public static User login(String accountID, String password) throws PasswordIncorrectException,
            AccountDoesNotExistException {
        try (BufferedReader bfr = new BufferedReader(new FileReader(accountFilename))) {
            String line = "";
            while ((line = bfr.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals(accountID)) {
                    if (parts[1].equals(password)) {
                        User u = new User(parts[0], parts[1], parts[2]);
                        u.downloadUser();
                        return u;
                    } else {
                        throw new PasswordIncorrectException("Password is not correct.");
                    }
                } 
                
            }
            throw new AccountDoesNotExistException("Account does not exist, please create one.");
            
            
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean accountExist(String accountID) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(accountFilename))) {
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

    public static void sendMessage(String sender, String receiver, String content) throws ReceiverNotFoundException, ReceiverBlockedException {

        if (!accountExist(receiver)) {
            throw new ReceiverNotFoundException("Receiver does not exist.");
        }

        User senderUser = new User(sender, sender + ".txt");
        senderUser.downloadUser(); // Load sender's data

        if (senderUser.hasBlocked(receiver)) {
            throw new ReceiverBlockedException("You have blocked the receiver.");
        }

        Message message = new Message(sender, receiver, content, new Date());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(message.getFilename(), true))) {
            writer.write(message.getSender() + " " + message.getReceiver() + " " +
                    message.getContent() + "\0" + message.getTimestamp() + "\n");
        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        }
    }

    public static void deleteMessage(String sender, String receiver, Date timestamp, String deleter) throws IOException, MessageDeletionException {
        String filename = getMessageFilename(sender, receiver);
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("Message file not found.");
        }

        File tempFile = new File(filename + ".tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            boolean deleted = false;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String messageSender = parts[0]; // Assuming sender is the second part
                String[] moreParts = line.split("\0");
                String messageTimestamp = moreParts[1];

                if (messageSender.equals(sender) && messageTimestamp.equals(Long.toString(timestamp.getTime()))) {
                    deleted = true;
                    continue; // Skip writing this line (message) to the temporary file
                }

                writer.println(line);
            }

            if (!deleted) {
                throw new MessageDeletionException("Message not found or you don't have permission to delete it.");
            }
        } catch (IOException | MessageDeletionException e) {
            throw new MessageDeletionException("Failed to delete message due to an IO error.", e);
        }

        // Replace the original file with the temporary file
        if (!file.delete()) {
            throw new IOException("Failed to delete the original file.");
        }
        if (!tempFile.renameTo(file)) {
            throw new IOException("Failed to rename the temporary file.");
        }
    }

    static class MessageDeletionException extends Exception {
        public MessageDeletionException(String message) {
            super(message);
        }

        public MessageDeletionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static String getMessageFilename(String sender, String receiver) {
        if (sender.compareTo(receiver) < 0) {
            return sender + receiver + ".txt";
        } else if (sender.compareTo(receiver) > 0) {
            return receiver + sender + ".txt";
        } else {
            return null;
        }
    }

    static class ReceiverNotFoundException extends Exception {
        public ReceiverNotFoundException(String message) {
            super(message);
        }
    }

    static class ReceiverBlockedException extends Exception {
        public ReceiverBlockedException(String message) {
            super(message);
        }
    }

    public static void addFriend(User user, String accountID) throws AreFriendException {
        ArrayList<String> myFriendList = user.getFriendsList();
        for (int i = 0; i < myFriendList.size(); i++) {
            if (accountID.equals(myFriendList.get(i))) {
                throw new AreFriendException("You are already friends with this user!");
            } else {
                myFriendList.add(accountID);
            }
        }
        user.uploadUser();
    }

    public static void removeFriend(User user, String accountID) throws NotFriendException {
        ArrayList<String> myFriendList = user.getFriendsList();
        for (int i = 0; i < myFriendList.size(); i++) {
            if (accountID.equals(myFriendList.get(i))) {
                myFriendList.remove(accountID);
            } else {
                throw new NotFriendException("You are not friends with this user.");
            }
        }
        user.uploadUser();
    }

    public static void addBlock(User user, String accountID) throws HaveBlockException {
        ArrayList<String> myBlockList = user.getBlockList();
        for (int i = 0; i < myBlockList.size(); i++) {
            if (accountID.equals(myBlockList.get(i))) {
                throw new HaveBlockException("You already have this user blocked!");
            } else {
                myBlockList.add(accountID);
            }
        }
        user.uploadUser();
    }

    public static void removeBlock(User user, String accountID) throws NotBlockException {
        ArrayList<String> myBlockList = user.getBlockList();
        for (int i = 0; i < myBlockList.size(); i++) {
            if (accountID.equals(myBlockList.get(i))) {
                throw new NotBlockException("You do not have this user blocked.");
            } else {
                myBlockList.remove(accountID);
            }
        }
        user.uploadUser();
    }

    public static void changeUsername(User user, String newName) throws NameSameException {
        if (newName.equals(user.getUserName())) {
            throw new NameSameException("You can't have the new username the same as the old one!");
        } else {
            user.setUserName(newName);
        }
    }

    public static void changePassword(User user, String newPassword) throws NameSameException {
        if (newPassword.equals(user.getPassword())) {
            throw new NameSameException("You can't have the new password the same as the old one!");
        } else {
            user.setPassword(newPassword);
        }
    }
}
