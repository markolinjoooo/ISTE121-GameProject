
/**
 * Final Game Project
 * .java
 * @author Marko Antoljak
 */
import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.animation.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Main class that starts the game
 */
public class Game extends Application implements EventHandler<KeyEvent> {
   // Window attributes
   private Stage stage;
   private Scene scene;
   private StackPane root;
   private Stage loadingStage;
   private Stage multiplayerStage;
   private Stage chatStage;
   public int numOfGhosts;
   private int coinsEaten = 0;
   private static String[] args;
   private static Clip bacgroundClip;
   private Rectangle rectanglePlanet;

   // CONNECTIVITY ATTRIBUTES
   private ObjectOutputStream oos = null;
   private ObjectInputStream ois = null;
   private Socket socket = null;
   public static final int SERVER_PORT = 1234;
   public static final String IP_ADDRESS = "127.0.0.1";
   private String clientName;

   public int numOfPlayers = 0;

   // images and class objects
   private ImageView backgroundImage;
   private ImageView bacgroundInvert;
   private int iconWidth;
   private int iconHeight;
   private Pacman racer;
   public Star star;
   public Planet planets;
   public Ghost ghost;
   private Image rgbBackground = null;
   private AnimationTimer timer;

   Button btnConnect = new Button("Connect");
   TextField tfClientName = new TextField();

   // chat attributes
   public Button btnSend = new Button("Send");
   public Button btnQuit = new Button("Quit");
   public TextArea textArea = new TextArea();
   public TextField tfToSend = new TextField();
   public FlowPane flowPane = new FlowPane(15, 15);
   public TextArea taMultiPlayer;
   // screen
   public Scene sceneChat;

   private ArrayList<Ghost> ghostList = new ArrayList<>();
   private ArrayList<Planet> planetList = new ArrayList<>();

   /**
    * main
    * 
    * @param _args
    */
   public static void main(String[] _args) {
      args = _args;
      launch(args);
   }

   /**
    * start screen
    * 
    * @param _stage
    */
   public void start(Stage _stage) {

      // stage seteup
      stage = _stage;
      stage.setTitle("Pac-Man Space");
      stage.setOnCloseRequest(
            new EventHandler<WindowEvent>() {
               public void handle(WindowEvent evt) {
                  System.exit(0);
               }
            });

      // root pane
      root = new StackPane();

      try {
         backgroundImage = new ImageView(new Image(new FileInputStream("pacman-mapa.png")));
         root.getChildren().addAll(backgroundImage);
         bacgroundInvert = new ImageView(new Image(new FileInputStream("backgroundEffect.gif")));

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      stage.setResizable(false);
      introScreen();
      playMusic();
   }

   /**
    * play background music
    */
   public static void playMusic() {
      try {
         // I MADE THIS SONG BYMYSELF - it is made in FL STUDIO
         AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Game_music.wav"));
         bacgroundClip = AudioSystem.getClip();

         bacgroundClip.open(audioStream);
         bacgroundClip.start();

      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   /**
    * play sound for collecting coins or colliding with ghosts
    * 
    * @param pathName
    */
   public void playSound(String pathName) {
      try {

         AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(pathName));
         Clip clip = AudioSystem.getClip();

         clip.open(audioStream);
         clip.start();

      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * introduction loading screen
    * 
    */
   public void introScreen() {

      // attributes
      Pane pane = new Pane();
      FlowPane pane2 = new FlowPane(10, 10);
      Button btnStart = new Button("Start");
      Button btnAbout = new Button("How to Play");
      Button btnQuit = new Button("Quit");
      Button btnMultiPlayer = new Button("Multi-Player");

      // screen
      Scene loadingScreen;

      ImageView picture = null;
      try {
         picture = new ImageView(new Image(new FileInputStream("pacmanLoading.gif")));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      // STYLING
      btnStart.setTextFill(Color.BLUE);
      btnQuit.setTextFill(Color.RED);
      btnMultiPlayer.setTextFill(Color.MAGENTA);

      // set buttons on action
      btnStart.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent ae) {

            loadingStage.close();
            coinsEaten = 0;
            startScene(1);
         }
      });

      btnMultiPlayer.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent ae) {

            multiPlayerScreen();
         }
      });
      btnQuit.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent ae) {

            System.exit(0);
         }
      });
      btnAbout.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent ae) {
            Alert howToAlert = new Alert(AlertType.INFORMATION);
            howToAlert.setTitle("LEARN HOW TO PLAY");
            howToAlert.setContentText(
                  "The goal of the game is to eat all the blue planets. If you collide with the ghost you are dead. There are 3 levels of the game. Each level up, the number of ghosts increases. When you click SPACE you can chat. Good luck!");
            howToAlert.setHeaderText("SPACE PACMAN GAME!");
            howToAlert.showAndWait();
         }
      });

      loadingStage = new Stage();
      pane.getChildren().addAll(picture, pane2);
      pane2.getChildren().addAll(btnStart, btnMultiPlayer, btnAbout, btnQuit);
      pane2.setAlignment(Pos.CENTER);
      loadingScreen = new Scene(pane);
      loadingStage.setScene(loadingScreen);
      loadingStage.setTitle("WELCOME TO PACMAN");
      loadingStage.show();

   }

   /**
    * start server method
    */
   public void startServer() {
      // starting server
      Server server = new Server();
      server.start(stage);
   }

   /**
    * open multiplayer screen
    */
   public void multiPlayerScreen() {

      startServer();
      // attributes
      VBox root = new VBox();
      FlowPane pane = new FlowPane(10, 10);
      FlowPane pane2 = new FlowPane(10, 10);
      Label label = new Label("Player name: ");
      taMultiPlayer = new TextArea();
      Scene multiplayerScreen;

      taMultiPlayer.setDisable(true);
      // STYLING
      btnConnect.setTextFill(Color.BLACK);

      // set buttons on action
      btnConnect.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent ae) {
            doMultiPlayer();
         }
      });

      pane.getChildren().addAll(label, tfClientName, btnConnect);
      pane2.getChildren().addAll(taMultiPlayer);
      pane.setAlignment(Pos.CENTER);
      pane2.setAlignment(Pos.CENTER);
      root.getChildren().addAll(pane, pane2);
      multiplayerScreen = new Scene(root);

      multiplayerStage = new Stage();
      multiplayerStage.setX(800);
      multiplayerStage.setY(300);
      multiplayerStage.setScene(multiplayerScreen);
      multiplayerStage.setTitle("MULTIPLAYER PACMAN GAME");
      multiplayerStage.show();
   }

   /**
    * connect to server and start sending information
    */
   public void doMultiPlayer() {

      try {
         socket = new Socket(IP_ADDRESS, SERVER_PORT);
         oos = new ObjectOutputStream(socket.getOutputStream());
         ois = new ObjectInputStream(socket.getInputStream());

         System.out.println("connected");
         clientName = tfClientName.getText();
         oos.writeUTF(clientName);
         oos.flush();

         // receiving messsage
         String connectionSuccess = ois.readUTF();

         System.out.println(connectionSuccess);
         Platform.runLater(new Runnable() {
            @Override
            public void run() {

               taMultiPlayer.appendText(connectionSuccess);
            }
         });

      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * alert for exceptions
    * 
    * @param alertType
    * @param message
    * @param header
    */
   public void alert(AlertType alertType, String message, String header) {
      Alert alert = new Alert(alertType);
      alert.setContentText(message);
      alert.setHeaderText(header);
      alert.showAndWait();
   }

   /**
    * appear screen for chat
    */
   public void chatScreen() {
      // attributes
      VBox root = new VBox(8);

      // set buttons on action
      btnSend.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent ae) {
            doChat();
         }
      });
      btnQuit.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent ae) {
            chatStage.close();
         }
      });
      textArea.setPrefSize(500, 350);
      tfToSend.setPrefSize(250, 50);
      flowPane.getChildren().addAll(tfToSend, btnSend);
      root.getChildren().addAll(textArea, flowPane, btnQuit);
      flowPane.setAlignment(Pos.BASELINE_CENTER);

      // initialize stage
      sceneChat = new Scene(root, 500, 500);
      chatStage = new Stage();

      // STYLING
      btnSend.setTextFill(Color.GREEN);
      btnQuit.setTextFill(Color.RED);
      chatStage.setResizable(false);
      textArea.setEditable(false);
      chatStage.setScene(sceneChat);
      chatStage.setTitle("CHAT");
      chatStage.setX(500);
      chatStage.setY(200);

      chatStage.show();

   }

   /**
    * sending and receiving chat messages
    */
   public void doChat() {

      String sentMessage = tfToSend.getText();
      try {
         oos.writeObject("CHAT");
         oos.flush();

         oos.writeUTF(sentMessage);
         oos.flush();

         String receivedMes = ois.readUTF();

         Platform.runLater(new Runnable() {
            public void run() {
               textArea.appendText(clientName + ": " + receivedMes + "\n");
            }
         });

      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   /**
    * start game and place all elements on screen
    * 
    * @param numOfGhosts
    */
   public void startScene(int numOfGhosts) {

      loadingStage.close();

      // new arraylists for cleaning
      ghostList = new ArrayList<>();
      planetList = new ArrayList<>();
      // check if ghosts are on the same area as coins
      checkIfCoinOnGhostArea();

      // put counter to 0
      coinsEaten = 0;

      this.numOfGhosts = numOfGhosts;
      // background for pixel reader
      try {
         rgbBackground = new Image(new FileInputStream("pacman-mapa-rgb.png"));

      } catch (Exception e) {
         System.out.println("Exception: " + e);
         return;
      }

      // ******* PUTTING ALL OBJECTS ON THE SCREEN ********* //

      // initiate class objects and put them on root
      // pacman start position
      racer = new Pacman(this);
      racer.setTranslateX(0);
      racer.setTranslateY(0);
      root.getChildren().add(racer);

      // star
      star = new Star();
      root.getChildren().add(star);

      double xStar = 0;
      double yStar = 0;

      while (!checkIfGhostOnPlayableArea(xStar, yStar, star.iconWidth,
            star.iconHeight)) {
         xStar = Math.random() * rgbBackground.getWidth();
         yStar = Math.random() * rgbBackground.getHeight();
      }
      star.setTranslateX(xStar);
      star.setTranslateY(yStar);

      // create and position ghostList

      for (int i = 0; i < numOfGhosts; i++) {
         ghost = new Ghost(this);
         ghostList.add(ghost);
         root.getChildren().add(ghost);

         double xGhost = 0;
         double yGhost = 0;

         // position random
         while (!checkIfGhostOnPlayableArea(xGhost, yGhost, ghost.iconWidth, ghost.iconHeight)) {
            xGhost = Math.random() * rgbBackground.getWidth();
            yGhost = Math.random() * rgbBackground.getHeight();
         }
         ghost.setTranslateX(xGhost);
         ghost.setTranslateY(yGhost);
      }

      // create and position planets
      for (int i = 0; i < 10; i++) {
         planets = new Planet();
         planetList.add(planets);
         root.getChildren().add(planets);

         double xPlanet = 0;
         double yPlanet = 0;

         // position random
         while (!checkIfPlanetOnPlayableArea(xPlanet, yPlanet, planets.iconWidth, planets.iconHeight)) {
            xPlanet = Math.random() * rgbBackground.getWidth();
            yPlanet = Math.random() * rgbBackground.getHeight();
         }

         planets.setTranslateX(xPlanet);
         planets.setTranslateY(yPlanet);
      }
      root.setId("pane");

      // display the window
      scene = new Scene(root, 1281, 720);

      stage.setScene(scene);
      stage.show();
      scene.setOnKeyPressed(this);
      System.out.println("Starting race...");

      // Use an animation to update the screen
      timer = new AnimationTimer() {
         public void handle(long now) {
            // pacman update
            racer.update();

            // update each ghost
            for (Ghost ghost : ghostList) {
               ghost.update();
            }

            // update each planet - coin
            for (Planet planet : planetList) {
               planets.update();
            }
            // update star
            star.update();

            // CHECK COLLISON WITH OTHERS
            intersectionWithPlanet();
            intersectionWithGhost();
            intersectionWithStar();

         }

      };

      // TimerTask to delay start of race for 2 seconds
      TimerTask task = new TimerTask() {
         public void run() {
            timer.start();
         }
      };
      Timer startTimer = new Timer();
      long delay = 100L;
      startTimer.schedule(task, delay);
   }

   /**
    * alert for game over
    * 
    * @param message
    */
   public void gameOverAlert(String message) {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("GAME OVER");
      alert.setHeaderText("Try Again?");
      alert.setContentText(message);
      alert.showAndWait();
   }

   /**
    * alert for game won
    * 
    * @param message
    */
   public void gameWonAlert(String message) {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle("YOU WON");
      alert.setContentText(message);
      alert.setTitle("SUCCESS!");
      alert.showAndWait();
   }

   /**
    * checking if element is collided with wall
    * 
    * @param x
    * @param y
    * @param width
    * @param height
    * @return
    */
   public boolean checkWallCollison(double x, double y, double width, double height) {
      PixelReader pr = rgbBackground.getPixelReader();
      // go thru both x and y
      for (int i = (int) x; i < x + 50; i++) {

         for (int j = (int) y; j < y + 30; j++) {
            // get color and set validation
            Color color = pr.getColor(i, j);
            // System.out.println(String.format("Red: %.2f Green: %.2f Blue: %.2f",
            // color.getRed(), color.getGreen(),
            // color.getBlue()));
            if (color.getRed() == 1.0 && color.getBlue() + color.getGreen() < 0.02) {
               return true;
            }
         }
      }
      return false;

   }

   /**
    * checking if ghost is not spawn close to pacman starting position
    * 
    * @param x
    * @param y
    * @param width
    * @param height
    * @return
    */
   public boolean checkIfGhostOnPlayableArea(double x, double y, double width, double height) {

      PixelReader pr = rgbBackground.getPixelReader();
      boolean noRed = true;

      for (int i = (int) x - 10; i < x + height + 20; i++) {

         for (int j = (int) y - 10; j < y + width + 20; j++) {
            try {
               Color color = pr.getColor(i, j);
               if (color.getBlue() > 0.9 && color.getRed() + color.getGreen() < 0.02) {

               } else {
                  noRed = false;
               }
            } catch (Exception e) {
               return false;
            }
         }
      }
      return noRed;
   }

   /**
    * checking intersection with ghost
    */
   public void intersectionWithGhost() {

      Ghost ghostToRemove = null;
      // go thru all ghosts and check if it collides
      for (Ghost ghost : ghostList) {
         double ghostX = ghost.getTranslateX();
         double ghostY = ghost.getTranslateY();
         double pacmanX = racer.racerImageView.getTranslateX();
         double pacmanY = racer.racerImageView.getTranslateY();

         // create rectangles and watch them intersect
         Rectangle rectanglePacman = new Rectangle(pacmanX, pacmanY, 50, 50);
         Rectangle rectangleGhost = new Rectangle(ghostX, ghostY, ghost.iconWidth, ghost.iconHeight);

         // validation
         if (rectanglePacman.intersects(rectangleGhost.getBoundsInLocal())) {
            System.out.println("COLLISON WITH GHOST");
            // ghostList.remove(ghost);

            playSound("soundUgh.wav");
            ghostToRemove = ghost;
            root.getChildren().remove(racer);
            root.getChildren().remove(rectangleGhost);
            for (Planet planet : planetList) {
               root.getChildren().remove(planet);
               root.getChildren().remove(rectanglePlanet);
            }
            bacgroundClip.stop();
            timer.stop();
            numOfGhosts = 1;
            Platform.runLater(new Runnable() {

               @Override
               public void run() {
                  gameOverAlert("Eaten by Ghost :(");
               }

            });

            stage.close();
            start(stage);
            startScene(numOfGhosts);
         }

      }

      if (ghostToRemove != null) {
         ghostList.remove(ghostToRemove);
      }

   }

   /**
    * checking intersection with the star
    */
   public void intersectionWithStar() {

      double starX = star.getTranslateX();
      double starY = star.getTranslateY();
      double pacmanX = racer.racerImageView.getTranslateX();
      double pacmanY = racer.racerImageView.getTranslateY();

      Rectangle rectanglePacman = new Rectangle(pacmanX, pacmanY, 50, 50);
      Rectangle rectangleStar = new Rectangle(starX, starY, 30, 30);

      if (rectanglePacman.intersects(rectangleStar.getBoundsInLocal())) {
         root.getChildren().remove(star);
         root.getChildren().remove(backgroundImage);
         root.getChildren().remove(rectangleStar);
         root.getChildren().set(0, bacgroundInvert);
         root.getChildren().remove(racer);
         root.getChildren().add(racer);

      }

   }

   /**
    * checking intersection with coins
    */
   public void intersectionWithPlanet() {

      Planet planetToRemove = null;
      for (Planet planet : planetList) {
         double planetX = planet.getTranslateX();
         double planetY = planet.getTranslateY();
         double pacmanX = racer.racerImageView.getTranslateX();
         double pacmanY = racer.racerImageView.getTranslateY();

         Rectangle rectanglePacman = new Rectangle(pacmanX, pacmanY, 50, 50);
         rectanglePlanet = new Rectangle(planetX, planetY, planet.iconWidth, planet.iconHeight);

         if (rectanglePacman.intersects(rectanglePlanet.getBoundsInLocal())) {

            playSound("coinSound.wav");
            planetToRemove = planet;
            // planetList.remove(planet);
            root.getChildren().remove(planet);
            root.getChildren().remove(rectanglePlanet);
            coinsEaten++;

         }

         if (numOfGhosts == 4) {
            timer.stop();
            numOfGhosts = 0;
            coinsEaten = 0;
            bacgroundClip.stop();
            playSound("GameWonSound.wav");
            Platform.runLater(new Runnable() {

               @Override
               public void run() {
                  gameWonAlert("YOU WON THE GAME");
               }
            });

            stage.close();
         }

         // level won if all coins eaten
         if (coinsEaten == 10) {
            coinsEaten = 0;
            timer.stop();
            bacgroundClip.stop();
            playSound("successSound.wav");

            System.out.println(coinsEaten + "");
            Platform.runLater(new Runnable() {
               @Override
               public void run() {
                  gameWonAlert("Level passed, next level?");
               }
            });
            stage.close();
            numOfGhosts++;
            start(stage);
            startScene(numOfGhosts);

         }
      }

      if (planetToRemove != null) {
         planetList.remove(planetToRemove);
      }
   }

   /**
    * checking if planet is not spawn close to pacman starting position
    * 
    * @param x
    * @param y
    * @param width
    * @param height
    * @return
    */
   public boolean checkIfPlanetOnPlayableArea(double x, double y, double width, double height) {
      PixelReader pr = rgbBackground.getPixelReader();
      boolean noRed = true;

      for (int i = (int) x - 10; i < x + height + 20; i++) {

         for (int j = (int) y - 10; j < y + width + 20; j++) {
            try {
               Color color = pr.getColor(i, j);
               if (color.getBlue() > 0.9 && color.getRed() + color.getGreen() < 0.02) {

               } else {
                  noRed = false;
               }
            } catch (Exception e) {
               return false;
            }
         }
      }
      return noRed;
   }

   /**
    * checkin if coin is not spawn on ghost
    */
   public void checkIfCoinOnGhostArea() {

      // putting planet spawn not so close to ghost
      for (Planet planet : planetList) {
         double planetX = planet.getTranslateX();
         double planetY = planet.getTranslateY();
         double ghostX = ghost.getTranslateX();
         double ghostY = ghost.getTranslateY();

         Rectangle rectangleGhost = new Rectangle(ghostX, ghostY, ghost.iconWidth + 20, ghost.iconHeight + 20);
         Rectangle rectanglePlanet = new Rectangle(planetX, planetY, planet.iconWidth, planet.iconHeight);

         if (rectangleGhost.intersects(rectanglePlanet.getBoundsInLocal())) {

            planetX += 100;

         }
      }
   }

   /**
    * handling keys method
    */
   public void handle(KeyEvent ke) {
      switch (ke.getCode()) {
         case S:
         case DOWN:
            racer.goDOWN();
            if (checkWallCollison(racer.racePosX, racer.racePosY, racer.iconWidth, racer.iconHeight)) {
               racer.racePosY -= racer.MOVING_SPEED;
            }

            break;
         case W:
         case UP:
            racer.goUP();
            if (checkWallCollison(racer.racePosX, racer.racePosY, racer.iconWidth, racer.iconHeight)) {
               racer.racePosY += racer.MOVING_SPEED;
            }
            break;
         case A:
         case LEFT:
            racer.goLEFT();
            if (checkWallCollison(racer.racePosX, racer.racePosY, racer.iconWidth, racer.iconHeight)) {
               racer.racePosX += racer.MOVING_SPEED;
            }
            break;
         case D:
         case RIGHT:
            racer.goRIGHT();
            if (checkWallCollison(racer.racePosX, racer.racePosY, racer.iconWidth, racer.iconHeight)) {
               racer.racePosX -= racer.MOVING_SPEED;
            }
            break;
         case SPACE:
            chatScreen();
            break;
      }
   }

} // end class Races
