import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 * A program that implements PhotoMessage Interface
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
public interface PhotoMessageInterface {

    // Returns the image associated with this photo message.
    BufferedImage getImage();

    // Saves the image to the file system using the previously generated filename.
    // The image is saved in PNG format.
    void writePhotoMessage();

    // Returns a string representation of the photo message, which includes the type,
    // timestamp, sender, receiver, and filename of the image.
    String toString();
}
