
// I USED THIS FROM INCLASS
import java.io.*;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.geometry.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Server class for server modifications
 */
public class Server extends Application implements EventHandler<ActionEvent> {
    // Window Attributes
    private Stage stage;
    private Scene scene;
    private VBox root = null;

    // GUI components

    TextArea taServer = new TextArea();

    // socket
    public static final int SERVER_PORT = 1234;
    public static final String IP_ADDRESS = "localhost";
    // List<ObjectOutputStream> nameOfWriters = new ArrayList<>();
    int clientIDCounter = 0;

    /**
     * main
     * 
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * start method
     * 
     * @param _stage
     */
    public void start(Stage _stage) {

        stage = _stage;
        stage.setTitle("SERVER side");
        final int WIDTH = 450;
        final int HEIGHT = 400;
        final int X = 200;
        final int Y = 100;

        stage.setX(X);
        stage.setY(Y);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent evt) {
                System.exit(0);
            }
        });

        // Set up root
        root = new VBox();
        taServer.setDisable(true);

        root.getChildren().addAll(taServer);
        // Set the scene and show the stage
        scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();

        // do Server Stuff
        doServerStuff();
    }

    /**
     * start server thread
     */
    private void doServerStuff() {
        ServerThread st = new ServerThread();
        st.start();
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                taServer.appendText("Starting Server on port " + SERVER_PORT + "...\n");
            }

        });
    }

    /**
     * inner class server thread handling
     */
    class ServerThread extends Thread {
        @Override
        public void run() {
            try {
                System.out.println("Openning SOCKET PORT");
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        taServer.appendText("Waiting client to connect...\n");
                    }

                });
                ServerSocket sSocket = new ServerSocket(SERVER_PORT);

                while (true) {
                    Socket cSocket = sSocket.accept();
                    ClientThread cT = new ClientThread(cSocket);
                    cT.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * inner class client thread handling
     */
    class ClientThread extends Thread {
        private Socket cSocket;
        private ObjectOutputStream oos = null;
        private ObjectInputStream ois = null;

        public ClientThread(Socket cSocket) {
            this.cSocket = cSocket;
        }

        @Override
        public void run() {
            try {
                this.ois = new ObjectInputStream(this.cSocket.getInputStream());
                this.oos = new ObjectOutputStream(this.cSocket.getOutputStream());

                // read client name input and append to server text area
                String clientName = ois.readUTF();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        taServer.appendText(clientName + " player connected\n");
                        clientIDCounter++;
                        taServer.appendText("Number of players connected: " + clientIDCounter);
                    }
                });

                oos.writeUTF(clientName + " - Connection successful!\n");
                oos.flush();

                while (true) {
                    try {

                        // reading command...
                        Object object = ois.readObject();

                        // cases
                        switch ((String) object) {
                            case "CHAT":
                                // read sent message
                                String message = ois.readUTF();
                                System.out.println(message);
                                // send message back to client
                                oos.writeUTF(message);
                                oos.flush();
                                break;

                            default:
                                break;
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                // while (true) {
                // Object obj = ois.readObject();
                // if (obj instanceof String) {
                // String message = (String) obj;
                // String[] arrayOfMessage = message.split("@");
                // if (arrayOfMessage.length == 2) {
                // switch (arrayOfMessage[0]) {
                // case "REGISTER":
                // System.out.println("REGISTER received");
                // oos.writeObject(clientIDCounter);
                // oos.flush();
                // clientIDCounter++;
                // break;
                // case "CHAT":
                // String chatMessage = arrayOfMessage[1];
                // // send to all connected clients
                // for (int i = 0; i < nameOfWriters.size(); i++) {
                // nameOfWriters.get(i).writeObject(chatMessage);
                // nameOfWriters.get(i).flush();
                // }
                // break;
                // }
                // }
                // } else if (obj instanceof Status) {
                // Status newStatus = (Status) obj;
                // for (int i = 0; i < nameOfWriters.size(); i++) {
                // // send to the others, and not back to me
                // if (nameOfWriters.get(i) != this.oos) {
                // nameOfWriters.get(i).writeObject(newStatus);
                // nameOfWriters.get(i).flush();
                // }
                // }
                // }

                // }
                // } catch (ClassNotFoundException cnfe) {
                // cnfe.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * alert for exceptions
     * 
     * @param type
     * @param message
     */
    public void showAlert(AlertType type, String message) {
        Platform.runLater(new Runnable() {
            public void run() {
                Alert alert = new Alert(type, message);
                alert.showAndWait();
            }
        });
    }

    @Override
    public void handle(ActionEvent arg0) {
        // TODO Auto-generated method stub

    }
}
