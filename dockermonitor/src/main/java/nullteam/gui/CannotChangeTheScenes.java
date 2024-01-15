package nullteam.gui;

import java.io.IOException;

public class CannotChangeTheScenes extends IOException {
    public CannotChangeTheScenes(){
        super("Cannot Change the Scene ...");
    }
    public CannotChangeTheScenes(String message) {
        super(message);
    }
}
