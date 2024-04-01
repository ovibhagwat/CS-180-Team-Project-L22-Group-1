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
    BufferedImage getImage();

    void writePhotoMessage();

    String toString();
}
