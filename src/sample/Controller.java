package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sample.VideoStream.LocalVideoStreams.ScreenStream;
import sample.VideoStream.LocalVideoStreams.WebCamStream;
import sample.VideoStream.VideoInputStream;
import sample.VideoStream.VideoOutputStream;

import javax.sound.sampled.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Controller {

    @FXML
    private ImageView imageView;

    @FXML
    private ImageView imageView1;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnConnect;

    @FXML
    private Button btnCreateServer;

    @FXML
    private Button btnCloseAllConnections;

    @FXML
    private TextField fieldIP;

    @FXML
    private TextArea areaInputText;

    @FXML
    private TextArea areaOutputText;

    @FXML
    private ToggleButton tbtnLeft;

    @FXML
    private ToggleButton tbtnCenter;

    @FXML
    private ToggleButton tbtnRight;

    @FXML
    private RadioButton radWebCam;

    @FXML
    private RadioButton radScreen;

    ServerSocket server;
    Socket socket;
    //BufferedWriter out;
    DataOutputStream out;
    DataInputStream in;
    int port = 8000;

    enum Status {SERVER, CLIENT}

    private Status status = null;

    enum Position {LEFT, CENTER, RIGHT}

    private void changePosition(Position pos) {
        if (pos == Position.LEFT) {
            imageView1.setLayoutX(25);
            imageView1.setLayoutY(35);
            imageView1.setFitWidth(960);
            imageView1.setFitHeight(610);

            imageView.setLayoutX(1036);
            imageView.setLayoutY(184);
            imageView.setFitWidth(456);
            imageView.setFitHeight(312);
        }
        if (pos == Position.CENTER) {
            imageView1.setLayoutX(50);
            imageView1.setLayoutY(125);
            imageView1.setFitWidth(735);
            imageView1.setFitHeight(450);

            imageView.setLayoutX(835);
            imageView.setLayoutY(125);
            imageView.setFitWidth(735);
            imageView.setFitHeight(450);
        }
        if (pos == Position.RIGHT) {
            imageView1.setLayoutX(25);
            imageView1.setLayoutY(184);
            imageView1.setFitWidth(456);
            imageView1.setFitHeight(312);

            imageView.setLayoutX(531);
            imageView.setLayoutY(35);
            imageView.setFitWidth(960);
            imageView.setFitHeight(610);
        }
    }

    private void playSound(String file) {
        try {
            File soundFile = new File(file);
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.setFramePosition(0);
            clip.start();

            Thread.sleep(clip.getMicrosecondLength() / 1000);
            clip.stop();
            clip.close();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
            exc.printStackTrace();
        } catch (InterruptedException exc) {
        }
    }

    VideoOutputStream videoOutputStream = null;
    VideoInputStream videoInputStream = null;

    @FXML
    void initialize() throws IOException {

        btnCreateServer.setOnAction(event -> {
            new Thread(() -> {
                try {
                    server = new ServerSocket(port);
                    socket = server.accept();
                    areaInputText.appendText("+1 Клиент\n");

                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());

                    status = Status.SERVER;

//                    String inputText;

//                    while (Main.run) {
//                        inputText = in.readUTF();
//                        areaInputText.appendText(inputText + "\n");
//                        playSound(Main.class.getResource("sound.wav").getFile());
//                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });

        btnConnect.setOnAction(event -> {

            new Thread(() -> {
                try {
                    socket = new Socket(fieldIP.getText(), port);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());

                    status = Status.CLIENT;

//                    new Thread(() -> {
//                        String inputText;

//                        while (Main.run) {
//                            try {
//                                inputText = in.readUTF();
//                                areaInputText.appendText(inputText + "\n");
//                                playSound(Main.class.getResource("sound.wav").getFile());
//                            } catch (IOException e) {
//                                System.exit(0);
//                            }
//                        }
//                    }).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        });

        btnStart.setOnAction(event -> {
//            WebCamStream.getInstance().startVideoStream(imageView);
//            ScreenStream.getInstance().startVideoStream(imageView1);

            if (status == null) return;

            if (status == Status.SERVER) {
                videoOutputStream = new VideoOutputStream(imageView1, 5013);
                videoOutputStream.start();
                videoInputStream = new VideoInputStream(imageView, fieldIP.getText(), 5014);
                videoInputStream.start();
            }

            if (status == Status.CLIENT) {
                videoOutputStream = new VideoOutputStream(imageView1, 5014);
                videoOutputStream.start();
                videoInputStream = new VideoInputStream(imageView, fieldIP.getText(), 5013);
                videoInputStream.start();
            }

        });

        btnStop.setOnAction(event -> {
//            WebCamStream.stopVideoStream();
//            ScreenStream.stopVideoStream();
        });

        btnCloseAllConnections.setOnAction(event -> {
            try {
                if (videoOutputStream != null) videoOutputStream.close();
                if (videoInputStream != null) videoInputStream.close();
                status = null;
                server.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Ошибка закрытия");
            }
        });

        radWebCam.setOnAction(event -> {
            if (videoOutputStream != null)
                videoOutputStream.changeType(VideoOutputStream.Type.WEBCAM);
        });
        radScreen.setOnAction(event -> {
            if (videoOutputStream != null)
                videoOutputStream.changeType(VideoOutputStream.Type.SCREEN);
        });

        tbtnLeft.setOnAction(event -> changePosition(Position.LEFT));
        tbtnCenter.setOnAction(event -> changePosition(Position.CENTER));
        tbtnRight.setOnAction(event -> changePosition(Position.RIGHT));

    }
}
