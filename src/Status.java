
import java.io.Serializable;
//Serialization - conversion of the state of an object into a byte stream

public class Status implements Serializable {
    private int ID;
    private int sliderStatus;

    public Status(int id, int sliderStatus) {
        this.ID = id;
        this.sliderStatus = sliderStatus;
    }

    int getID() {
        return ID;
    }

    int getSliderStatus() {
        return sliderStatus;
    }


}