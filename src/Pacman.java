import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.EventListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Pacman class for pacman modification
 */
class Pacman extends Pane implements EventListener {
   public int racePosX = 200; // x position of the racer
   public int racePosY = 550; // y position of the racer
   private int raceROT = 0; // x position of the racer
   public ImageView racerImageView;
   private Image pacmanImg = null;
   private Image pacmanImg2 = null;

   private Game game = null;

   public double iconWidth = 0;
   public double iconHeight = 0;

   public Pacman(Game game) {
      // Draw the icon for the racer
      this.game = game;
      try {
         pacmanImg = new Image(new FileInputStream("pacman.gif"));
         racerImageView = new ImageView(pacmanImg);
      } catch (Exception e) {
         System.out.println("Exception: " + e);
         System.exit(0);
      }

      this.getChildren().add(racerImageView);
      // Get image size
      iconWidth = (int) pacmanImg.getWidth();
      iconHeight = (int) pacmanImg.getHeight();
   }

   public void changeImg() {

      this.getChildren().remove(racerImageView);

      try {
         pacmanImg2 = new Image(new FileInputStream("pacmanLeft.gif"));
         racerImageView = new ImageView(pacmanImg2);
      } catch (Exception e) {
         System.out.println("Exception: " + e);
         System.exit(0);
      }

      this.getChildren().add(racerImageView);
   }

   public void firstImg() {

      this.getChildren().remove(racerImageView);

      try {
         pacmanImg2 = new Image(new FileInputStream("pacman.gif"));
         racerImageView = new ImageView(pacmanImg2);
      } catch (Exception e) {
         System.out.println("Exception: " + e);
         System.exit(0);
      }

      this.getChildren().add(racerImageView);
   }

   

   /**
    * update() method keeps the thread (racer) alive and moving.
    */
   public void update() {

      racerImageView.setTranslateX(racePosX);
      racerImageView.setTranslateY(racePosY);
      racerImageView.setRotate(raceROT);

   } // end update()

   // methods for movement
   public static final int MOVING_SPEED = 40;

   public void goUP() {
      racePosY = racePosY - MOVING_SPEED;
      raceROT = 270;
      firstImg();
   }

   public void goDOWN() {
      racePosY = racePosY + MOVING_SPEED;
      raceROT = 90;
      firstImg();
   }

   public void goLEFT() {
      racePosX = racePosX - MOVING_SPEED;
      raceROT = 0;
      changeImg();
   }

   public void goRIGHT() {
      racePosX = racePosX + MOVING_SPEED;
      raceROT = 0;
      firstImg();
   }

} // end inner class Racer
