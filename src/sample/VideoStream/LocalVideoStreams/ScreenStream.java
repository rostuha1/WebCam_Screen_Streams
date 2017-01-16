package sample.VideoStream.LocalVideoStreams;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import javafx.scene.image.Image;

import java.awt.*;

public class ScreenStream {
    boolean isRun = false;

    private static ScreenStream screenStream = null;

    private ScreenStream(){}

    public static ScreenStream getInstance(){
        return (screenStream != null) ? screenStream : (screenStream = new ScreenStream());
    }

    public synchronized Image getScreenShot(){
        Image image = null;
        try {
            image = SwingFXUtils.toFXImage(new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())), null);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void getScreenShot(ImageView imageView) {
        try {
            imageView.setImage(SwingFXUtils.toFXImage(new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())), null));
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void startVideoStream(ImageView imageView) {
        new Thread(() -> {
            if (!isRun) {
                isRun = true;
                try {
                    while (isRun)
                        imageView.setImage(SwingFXUtils.toFXImage(new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())), null));
//                    imageView.setImage(null);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopVideoStream() {
        isRun = false;
    }

}
