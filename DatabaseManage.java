import java.io.*;

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

    public static void addFriend(User user, String accountID) throws AreFriendException {
        ArrayList<String> myFriendList = user.getFriendsList();
        for (int i = 0; i < myFriendList.size(); i++) {
            if (accountID.equals(myFriendList.get(i))) {
                throw new AreFriendException("You are already friends with this user!");
            } else {
                myFriendList.add(accountID);
            }
        }
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
    }
}
