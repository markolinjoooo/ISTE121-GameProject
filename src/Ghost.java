import java.io.FileInputStream;
import java.util.EventListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Ghost class for ghost modification
 */
class Ghost extends Pane implements EventListener {
   private int racePosX = 0; // x position of the racer
   private int racePosY = 0; // y position of the racer
   private int raceROT = 0; // x position of the racer
   public ImageView ghostImgView; // a view of the icon ... used to display and move the image
   private Image ghostImg = null;
   private Game game = null;

   private double ghostSpeed = 5;
   private double direction = 0;

   public double iconWidth = 0;
   public double iconHeight = 0;

   public Ghost(Game game) {
      // Draw the icon for the racer

      this.game = game;
      try {
         ghostImg = new Image(new FileInputStream("enemy_ghost.png"));
         ghostImgView = new ImageView(ghostImg);
      } catch (Exception e) {
         System.out.println("Exception: " + e);
         System.exit(0);
      }
      this.getChildren().add(ghostImgView);

      // Get image size
      iconWidth = (int) ghostImg.getWidth();
      iconHeight = (int) ghostImg.getHeight();

      
   }

   /**
    * update() method keeps the thread (racer) alive and moving.
    */

   public void update() {

      // moving ghost
      //racePosX += (int) (Math.random() * iconWidth / 30);
      //racePosY += (int) (Math.random() * iconWidth / 30);
      //racePosX += (int) (Math.random() * (Math.sin(Math.toRadians(direction))) * ghostSpeed) * 10;
      //racePosY += (int) (Math.random() * (Math.cos(Math.toRadians(direction))) * ghostSpeed) * 10;

      if (racePosX < 0) {
         racePosX = 0;
      }
      if (racePosY < 0) {
         racePosY = 0;
      }

      ghostImgView.setTranslateX(racePosX);
      ghostImgView.setTranslateY(racePosY);
      ghostImgView.setRotate(direction);

      
   } // end update()

} // end inner class Racer
