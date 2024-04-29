import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * A program that implements PhotoMessage class
 *
 * <p>Purdue University -- CS18000 -- Spring 2024</p>
 *
 * @author Yixin Hu
 * @version April 1, 2024
 */
public class PhotoMessage extends Message implements PhotoMessageInterface, Serializable {

    private static final long serialVersionUID = 1L; // Adding a serialVersionUID
    // The image of the photo message.
    private transient BufferedImage image;
    // Filename where the photo message will be saved or has been loaded from.
    private String filename;

    // Constructs a PhotoMessage object with specified details and the path to the photo file. It reads the photo
    // from the given pathname to create a BufferedImage.
    public PhotoMessage(String type, String sender, String receiver, String content, String pathname) {
        super(type, sender, receiver, content);
        try {
            this.image = ImageIO.read(new File(pathname));
            this.filename = sender + "_" + receiver + "_" + getTimestamp().getTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Constructs a PhotoMessage object from a string representation, which includes the image filename.
    public PhotoMessage(String data) {
        super(data);
        this.filename = getContent();
        try {
            this.image = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns the image associated with this photo message.
    public BufferedImage getImage() {
        return image;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageIO.write(image, "PNG", out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = ImageIO.read(in);
    }

    // Saves the image to the file system using the previously generated filename.
    // The image is saved in PNG format.
    public void writePhotoMessage() {
        try {
            // Save the image in PNG format to the file system.
            ImageIO.write(image, "PNG", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns a string representation of the photo message, which includes the type,
    // timestamp, sender, receiver, and filename of the image.
    public String toString() {
        // Construct the string representation including the filename instead of the actual content.
        return getType() + "|" + getTimestamp().getTime() + "|" + getSender() + "|" + getReceiver() + "|" + filename;
    }
}

