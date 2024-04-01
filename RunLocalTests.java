import org.junit.Test;
import org.junit.After;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.Assert;
import org.junit.Before;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.*;
import java.io.*;

public class RunLocalTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Test ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
    
    public static class TestCase {
        private final PrintStream originalOutput = System.out;
        private final InputStream originalSysin = System.in;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayInputStream testIn;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOut;

        @Before
        public void outputStart() {
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut));
        }

        @After
        public void restoreInputAndOutput() {
            System.setIn(originalSysin);
            System.setOut(originalOutput);
        }

        private String getOutput() {
            return testOut.toString();
        }

        @SuppressWarnings("SameParameterValue")
        private void receiveInput(String str) {
            testIn = new ByteArrayInputStream(str.getBytes());
            System.setIn(testIn);
        }

        @Test(timeout = 1000)
        public void messageTest() {
            String sender = "Alice";
            String receiver = "Bob";
            String content = "Hello!";
            Date timestamp = new Date();
            Message message = new Message(sender, receiver, content, timestamp);
            String toStringTemp = "Message{" + "filename='" + filename + '\'' +
            ", sender='" + sender + '\'' + ", receiver='" + receiver + '\'' +
            ", content='" + content + '\'' + ", timestamp=" + timestamp + '}';
            assertNotNull(message);
            assertEquals(sender, message.getSender());
            assertEquals(receiver, message.getReceiver());
            assertEquals(content, message.getContent());
            assertEquals(timestamp, message.getTimestamp());
            assertEquals(toStringTemp, message.toString());
        } 

        @Test(timeout = 1000)
        public void filenameTest() {
            Message message1 = new Message("Alice", "Bob", "Hello", new Date());
            assertEquals("AliceBob.txt", message1.getFilename());

        // Test when sender > receiver
            Message message2 = new Message("Bob", "Alice", "Hello", new Date());
            assertEquals("AliceBob.txt", message2.getFilename());

        // Test when sender equals receiver
            Message message3 = new Message("Alice", "Alice", "Hello", new Date());
            assertNull(message3.getFilename());
        }

        public void testUsers() {
            try {
                ArrayList<User> newUsers = new ArrayList<User>();
                newUsers.add("mike123", "a123456", "mike123.txt");
                newUsers.add("Adam123", ":aAsdf", "Adam123.txt");
                newUsers.add("John5648", "asdf123", "John5648.txt");
                newUsers.get(0).setUserName("mike.");
                newUsers.get(1).setUserName("Adam");
                newUsers.get(2).setUserName("John!");
                ArrayList<String> friendsList = new Arraylist<String>();
                friendsList.add("mike123");
                for (int i = 1; i < newUsers.size(); i++) {
                    newUsers.get(i).setFriendList(friendsList);
                }
                ArrayList<String> blockList = new Arraylist<String>();
                blockList.add("Adam123");
                for (int i = 0; i < newUsers.size() - 2; i++) {
                    newUsers.get(i).setBlockList(friendsList);
                }
                newUsers.get(0).setUserProfile("This is mike.");
                newUsers.get(1).setUserProfile("I'm Adam. I like running!");
                newUsers.get(2).setUserProfile("Hey, my name is John!");
                assertNull(newUsers.get(0).getFriendList().get(0));
                assertNull(newUsers.get(2).getBlockList().get(0));
                assertEquals("John5648", newUsers.get(2).getUserName());
                assertEquals(true, newUsers.get(0).hasBlocked("Adam123"));
                for (int i = 0; i < newUsers.size() - 2; i++) {
                    newUsers.get(i).uploadUser();
                }
                for (int i = 0; i < newUsers.size() - 2; i++) {
                    newUsers.get(i).downloadUser();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }
        public void testDatabase() {
            try {
                User newUser = createAccount("John123", "asdf123");
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
            try {
                User loginUser = login("John123", "asdf123");
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
            assertEquals(true, accountExist("John123"));
            try {
                sendMessage("John123", "Adam123", "Hello");
            } catch (IOException e) {
                e.printStackTrace();
                fail();
            }
            Date timestamp = new Date();
            try {
                deleteMessage("John123", "Adam123", timestamp, "John123");
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
            assertEquals("AdamJohn.txt", getMessageFilename("John", "Adam"));
            try {
                addFriend(loginUser, "Adam123");
            } catch (AreFriendException e) {
                fail();
            }
            try {
                removeFriend(loginUser, "Adam123");
            } catch (NotFriendException e) {
                fail();
            }
            try {
                addBlock(loginUser, "Adam123");
            } catch (HaveBlockException e) {
                fail();
            }
            try {
                removeBlock(loginUser, "Adam123");
            } catch (NotBlockException e) {
                fail();
            }
            try {
                changeUsername(loginUser, "John1069");
            } catch (NameSameException e) {
                fail();
            } catch (IOException e) {
                fail();
            }
            try {
                changePassword(loginUser, "aabbccdd");
            } catch (NameSameException e) {
                fail();
            } catch (IOException e) {
                fail();
            }
            try {
                changeUserProfile(loginUser, "This is John!")
            } catch (IOException e) {
                fail();
            }
        }
    }

}

