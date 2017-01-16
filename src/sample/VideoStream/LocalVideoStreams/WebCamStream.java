package sample.VideoStream.LocalVideoStreams;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.VideoInputFrameGrabber;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WebCamStream {

    private FrameGrabber grabber = new VideoInputFrameGrabber(0);
    private Java2DFrameConverter converter = new Java2DFrameConverter();
    private boolean isRun = false;

    private static WebCamStream webCamStream = null;

    private WebCamStream() {
        grabber.setFrameRate(30);
    }

    public static WebCamStream getInstance() {
        return (webCamStream != null) ? webCamStream : (webCamStream = new WebCamStream());
    }

    public void start() {
        isRun = true;
        try {
            grabber.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {

        if (!isRun) return;

        isRun = false;
        try {
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    public Image getWebCamShot() {
        if (!isRun) {
            try {
                grabber.start();
                return SwingFXUtils.toFXImage(converter.convert(grabber.grab()), null);
            } catch (FrameGrabber.Exception ignored) {}
            finally {
                try {
                    grabber.stop();
                } catch (FrameGrabber.Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        } else {
            try {
                return SwingFXUtils.toFXImage(converter.convert(grabber.grab()), null);
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void getWebCamShot(ImageView imageView) {
        if (!isRun) {
            try {
                grabber.start();
                imageView.setImage(SwingFXUtils.toFXImage(converter.convert(grabber.grab()), null));
                grabber.stop();
            } catch (FrameGrabber.Exception ignored) {
            }
        } else System.err.println("WebCam is already in use");
    }

    public void startVideoStream(ImageView imageView) {
        new Thread(() -> {
            if (!isRun) {
                isRun = true;
                try {
                   grabber.start();
                    while (isRun)
                        imageView.setImage(SwingFXUtils.toFXImage(converter.convert(grabber.grabFrame()), null));
                    grabber.stop();
                    imageView.setImage(null);
                } catch (FrameGrabber.Exception ignored) {
                }
            }
        }).start();
    }

    public void stopVideoStream() {
        isRun = false;
    }

    public byte[] pictureToBytes(BufferedImage picture) throws FrameGrabber.Exception, IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageIO.write(picture, "jpg", bout);
        bout.flush();
        return bout.toByteArray();
    }

    public static BufferedImage bytesToPicture(byte[] bytes) throws FrameGrabber.Exception, IOException {
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

}
