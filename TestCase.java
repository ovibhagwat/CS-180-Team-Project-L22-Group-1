public class TestCase {
    public static void main(String[] args) {
        try {
            User u1 = DatabaseManage.createAccount("Amy1", "password1");
            if (u1 != null) {
                u1.setUserName("Amy S.");
                u1.uploadUser();
            }
            User u2 = DatabaseManage.createAccount("Amy1", "password2"); // cannot create
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            User u3 = DatabaseManage.createAccount("Bob2", "password3");
            if (u3 != null) {
                u3.setUserName("Bob B.");
                u3.uploadUser();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            User u1 = DatabaseManage.login("Amy1", "password"); // wrong password
            User u2 = DatabaseManage.login("Cathy3", "password4"); // account doesn't exist yet
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            User u3 = DatabaseManage.login("Amy1", "password1"); // login successfully
            User u4 = DatabaseManage.login("Bob2", "password3"); // login successfully
            if (u3 != null && u4 != null) {
                u3.getFriendsList().add("Bob2");
                u3.uploadUser();
                u4.getFriendsList().add("Amy1");
                u3.uploadUser();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            DatabaseManage.sendMessage("Amy1", "Bob2", "Hey Bob! How are you?");

            // Test sending a message from u2 to u1
            DatabaseManage.sendMessage("Bob2", "Amy1", "Hi Amy! I'm doing well, thanks!");

            // Test sending an empty message
            DatabaseManage.sendMessage("Amy1", "Bob2", "");

            // Test sending a message with special characters
            DatabaseManage.sendMessage("Bob2", "Amy1", "Hello Amy! ~!@#$%^&*()_+{}|:\"<>?");

            // Test sending a long message
            StringBuilder longMessage = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                longMessage.append("This is a long message. ");
            }
            DatabaseManage.sendMessage("Amy1", "Bob2", longMessage.toString());

            // Add more positive test cases as needed
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
