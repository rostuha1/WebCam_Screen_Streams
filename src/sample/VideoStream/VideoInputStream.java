package sample.VideoStream;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class VideoInputStream {

    private DataInputStream dataInputStream;
    private int port;
    private String ip;
    ImageView imageView;
    boolean isExist = false;

    public Image getImage() {
        Image image;
        Socket socket = null;
        try {
            while (socket == null) {
                try {
                    socket = new Socket(ip, port);
                } catch (Exception e) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e1) {
                    }
                }
            }

            dataInputStream = new DataInputStream(socket.getInputStream());
            image = SwingFXUtils.toFXImage(ImageIO.read(dataInputStream), null);
        } catch (IOException e) {
            image = null;
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    public VideoInputStream(ImageView imageView, int port) {  // for server
        this.port = port;
        this.imageView = imageView;
    }

    public VideoInputStream(ImageView imageView, String ip, int port) { // for client
        this.port = port;
        this.ip = ip;
        this.imageView = imageView;
    }

    public void start() {
        new Thread(() -> {
            isExist = true;
            while (isExist) {
                imageView.setImage(getImage());
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException ignored) {
//                }
            }
        }).start();
    }

    public void stop() {
        isExist = false;
    }

    public void close() {
        stop();
        try {
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
