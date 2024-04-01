import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 * A program that tests PhotoMessage class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
public class TestPhotoMessage {

    // Path to a test image file used in the tests.
    private static final String TEST_IMAGE_PATH = "testPic.png";
    private static File testImageFile;

    // Sets up the test class by ensuring a test image file exists before any tests are run.
    @BeforeClass
    public static void setupClass() {
        testImageFile = new File(TEST_IMAGE_PATH);
        // Ensure the test image exists
        Assert.assertTrue("Test image must exist", testImageFile.exists());
    }

    // Tests the PhotoMessage constructor, ensuring an image is loaded and the filename is constructed correctly.
    @Test
    public void constructorTest() throws IOException {
        String type = "photo";
        String sender = "Alice";
        String receiver = "Bob";
        String content = "";
        PhotoMessage photoMessage = new PhotoMessage(type, sender, receiver, content, TEST_IMAGE_PATH);

        // Verify the image has been loaded successfully and the filename is correct.
        Assert.assertNotNull("Image should be loaded", photoMessage.getImage());
        Assert.assertTrue("Filename should contain sender, receiver, and timestamp",
                photoMessage.toString().contains(sender) &&
                        photoMessage.toString().contains(receiver) &&
                        photoMessage.toString().contains(String.valueOf(photoMessage.getTimestamp().getTime())));
    }

    // Tests writing a PhotoMessage to a file and reading it back to verify the process works correctly.
    @Test
    public void writeAndReadImageTest() throws IOException {
        PhotoMessage photoMessage = new PhotoMessage("photo", "Alice123", "Bob456", "",
                TEST_IMAGE_PATH);
        // Write the photo message to a file
        photoMessage.writePhotoMessage();

        // Attempt to read the written image
        BufferedImage readImage = ImageIO.read(new File(photoMessage.toString().split("\\|")[4]));
        Assert.assertNotNull("The image should be read successfully", readImage);

        // Clean up the created file
        new File(photoMessage.toString().split("\\|")[4]).delete();
    }

    // Main method to run the test suite and print out the results.
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestPhotoMessage.class);
        if (result.wasSuccessful()) {
            System.out.println("All tests passed!");
        } else {
            // Print details of any failures.
            result.getFailures().forEach(failure -> System.out.println(failure.toString()));
        }
    }
}
