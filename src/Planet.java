import java.io.FileInputStream;
import java.util.EventListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
/**
 * planet class for coin modification
 */
class Planet extends Pane implements EventListener {
   private int racePosX = 0; // x position of the racer
   private int racePosY = 0; // y position of the racer
   private int raceROT = 0; // rotate position of the racer
   public ImageView planetImgView; // a view of the icon ... used to display and move the image

   private Image planetImg = null;

   public double iconWidth = 0;
   public double iconHeight = 0;

   public Planet() {
      // Draw the icon for the racer
      
      try {
         planetImg = new Image(new FileInputStream("planet.png"));
         planetImgView = new ImageView(planetImg);
      } catch (Exception e) {
         System.out.println("Exception: " + e);
         System.exit(0);
      }
      this.getChildren().add(planetImgView);

      // Get image size
      iconWidth = (int) planetImg.getWidth();
      iconHeight = (int) planetImg.getHeight();
   }

   /**
    * update() method keeps the thread (racer) alive and moving.
    */
   public void update() {
      planetImgView.setTranslateX(racePosX);
      planetImgView.setTranslateY(racePosY);
      planetImgView.setRotate(raceROT);
      raceROT+= 1 / 20;

   } // end update()

} // end inner class Racer
