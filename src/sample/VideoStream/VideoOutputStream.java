package sample.VideoStream;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sample.VideoStream.LocalVideoStreams.ScreenStream;
import sample.VideoStream.LocalVideoStreams.WebCamStream;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

public class VideoOutputStream {

    public static synchronized void setImage(ImageView imageView, Image image) {
        imageView.setImage(image);
    }

    private DataOutputStream dataOutputStream;
    private ImageView imageView;
    private boolean isExist = false;
    private int port;
    private String ip;

    public enum Type {WEBCAM, SCREEN}

    private Type type = Type.SCREEN;

    public VideoOutputStream(ImageView imageView, int port) {  // for server
        this.port = port;
        this.imageView = imageView;
    }

    public VideoOutputStream(ImageView imageView, String ip, int port) {  // for client
        this.port = port;
        this.ip = ip;
        this.imageView = imageView;
    }

    private void sendImage() {
        sendImage(imageView.getImage());
    }

    public void sendImage(Image image) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            dataOutputStream = new DataOutputStream(serverSocket.accept().getOutputStream());
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "jpeg", bout);
            bout.flush();
            bout.close();
            dataOutputStream.write(bout.toByteArray(), 0, bout.toByteArray().length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        new Thread(() -> {
            isExist = true;
            while (isExist) {
                if (type == Type.SCREEN) imageView.setImage(ScreenStream.getInstance().getScreenShot());
                if (type == Type.WEBCAM) imageView.setImage(WebCamStream.getInstance().getWebCamShot());
                if (imageView.getImage() != null)
                    sendImage(imageView.getImage());
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException ignored) {
//                    }
            }
        }).start();
    }

    public void changeType(Type type) {
        this.type = type;
        if (type == Type.WEBCAM) {
            WebCamStream.getInstance().start();
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException ignored) {}
        }
        if (type == Type.SCREEN) WebCamStream.getInstance().stop();
    }

    public void stop() {
        isExist = false;
    }

    public void close() {
        stop();
        WebCamStream.getInstance().stop();
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
