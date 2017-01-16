package sample.VideoStream.LocalVideoStreams;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PictureConverter {

    public static byte[] pictureToBytes(BufferedImage image) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", bout);
            bout.flush();
            return bout.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage bytesToPicture(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
