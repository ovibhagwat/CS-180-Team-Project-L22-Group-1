import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * A program that tests User class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
@RunWith(JUnit4.class)
public class TestUser {

    // TemporaryFolder rule to create temporary files/directories that are deleted when tests finish.
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    // Tests the User constructor with parameters for correct object creation.
    @Test
    public void constructorWithParametersTest() {
        User user = new User("Amy123", "pass", "Amy123.txt");
        Assert.assertNotNull("User object should be created", user);
    }

    // Tests getter methods to ensure they correctly return the expected values.
    @Test
    public void gettersTest() {
        User user = new User("Amy123", "password", "Amy123.txt");
        user.setUserName("Amy");
        user.setUserProfile("I am Amy");
        Assert.assertEquals("Username should match", "Amy123", user.getAccountID());
        Assert.assertEquals("Username should match", "password", user.getPassword());
        Assert.assertEquals("Username should match", "Amy", user.getUserName());
        Assert.assertEquals("Username should match", "I am Amy", user.getUserProfile());
    }

    // Tests the ability to change a user's name and persist the change.
    @Test
    public void changeUserNameTest() throws IOException {
        File tempFile = folder.newFile("Amy123.txt");
        User user = new User("Amy123", "password", tempFile.getAbsolutePath());
        user.changeUserName("NewName");
        Assert.assertEquals("Username should be updated", "NewName", user.getUserName());
    }

    // Tests changing a user's profile description and ensuring it's correctly updated.
    @Test
    public void changeProfileTest() throws IOException {
        File tempFile = folder.newFile("Amy123.txt");
        User user = new User("Amy123", "password", tempFile.getAbsolutePath());
        user.setUserProfile("I am Amy");
        user.changeUserProfile("NewProfile");
        Assert.assertEquals("Username should be updated", "NewProfile", user.getUserProfile());
    }

    // Tests reading user data from a file to ensure all user attributes are correctly populated.
    @Test
    public void readUserTest() throws IOException {
        // Create a temporary file to simulate the user's data file
        File tempFile = folder.newFile("Amy123.txt");
        // Write expected data to the file
        try (PrintWriter out = new PrintWriter(new FileWriter(tempFile))) {
            out.println("Amy S."); // userName
            out.println("I am Amy."); // userProfile
            out.println("[friend1, friend2]"); // friendsList
            out.println("[blocked1, blocked2]"); // blockList
            out.println("[conv1.txt, conv2.txt]"); // conversationFilenames
        }

        // Instantiate a User object with the path to the temporary file
        User user = new User("Amy123") {
            @Override
            public String getFilename() {
                return tempFile.getAbsolutePath();
            }
        };
        // Call readUser to populate the User object with data from the file
        user.readUser();
        // Assert that the User object's state matches the expected values
        Assert.assertEquals("Amy S.", user.getUserName());
        Assert.assertEquals("I am Amy.", user.getUserProfile());
        Assert.assertTrue(user.getFriendsList().containsAll(new ArrayList<>(Arrays.asList("friend1", "friend2"))));
        Assert.assertTrue(user.getBlockList().containsAll(new ArrayList<>(Arrays.asList("blocked1", "blocked2"))));
        Assert.assertTrue(user.getConversationFilenames().containsAll(new ArrayList<>(Arrays.asList("conv1.txt",
                "conv2" + ".txt"))));
    }

    // Tests writing user data to a file and verifying the contents match expected values.
    @Test
    public void writeUserTest() throws IOException {
        File testFile = folder.newFile("Amy123.txt");
        User user = new User("Amy123", "password", testFile.getAbsolutePath());
        user.setUserName("Amy");
        user.setUserProfile("I am Amy");
        try {
            user.addFriend("Bob456");
            user.addBlock("Cathy135");
        } catch (FriendBlockErrorException e) {
            e.printStackTrace();
        }
        user.writeUser();
        // Read the file to verify
        try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
            Assert.assertEquals("First line should be the username", "Amy", reader.readLine());
            Assert.assertEquals("Second line should be the profile", "I am Amy", reader.readLine());
            Assert.assertEquals("Third line should be the friend list", "[Bob456]", reader.readLine());
            Assert.assertEquals("Fourth line should be the block list", "[Cathy135]", reader.readLine());
            Assert.assertEquals("Fifth line should be the conversation list", "[]", reader.readLine());
        }
    }

    // Verifies adding and removing friends updates the user's friend list appropriately.
    @Test
    public void addAndRemoveFriendTest() throws FriendBlockErrorException, IOException {
        File tempFile = folder.newFile("Amy123.txt");
        User user = new User("Amy123", "password", tempFile.getAbsolutePath());
        user.addFriend("Bob456");
        Assert.assertTrue("Friend list should contain 'Bob456'", user.getFriendsList().contains("Bob456"));
        user.removeFriend("Bob456");
        Assert.assertFalse("Friend list should not contain '456'", user.getFriendsList().contains("Bob456"));
    }

    // Verifies adding a friend who is already a friend or removing a non-friend throws an exception.
    @Test(expected = FriendBlockErrorException.class)
    public void addAndRemoveFriendErrorTest() throws FriendBlockErrorException, IOException {
        File tempFile = folder.newFile("Amy123.txt");
        User user = new User("Amy123", "password", tempFile.getAbsolutePath());
        user.addFriend("Bob456");
        user.addFriend("Bob456");
        user.removeFriend("Cathy135");
    }

    // Tests for adding and removing blocks to ensure the user's block list is updated correctly.
    @Test
    public void addAndRemoveBlockTest() throws FriendBlockErrorException, IOException {
        File tempFile = folder.newFile("Amy123.txt");
        User user = new User("Amy123", "password", tempFile.getAbsolutePath());
        user.addBlock("Bob456");
        Assert.assertTrue("Block list should contain 'Bob456'", user.getBlockList().contains("Bob456"));
        user.removeBlock("Bob456");
        Assert.assertFalse("Block list should not contain 'Bob456'", user.getBlockList().contains("Bob456"));
    }

    // Tests error handling when adding a block that already exists or removing a non-block.
    @Test (expected = FriendBlockErrorException.class)
    public void addAndRemoveBlockErrorTest() throws FriendBlockErrorException, IOException {
        File tempFile = folder.newFile("Amy123.txt");
        User user = new User("Amy123", "password", tempFile.getAbsolutePath());
        user.addBlock("Bob456");
        user.addBlock("Bob456");
        user.removeBlock("Cathy135");
    }

    // Main method to run all tests and output results.
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestUser.class);
        if (result.wasSuccessful()) {
            System.out.println("All tests passed!");
        } else {
            result.getFailures().forEach(failure -> System.out.println(failure.toString()));
        }
    }
}
