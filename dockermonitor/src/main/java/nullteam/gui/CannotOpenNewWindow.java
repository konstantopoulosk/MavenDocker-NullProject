package nullteam.gui;

import java.io.IOException;

public class CannotOpenNewWindow extends IOException {
    public CannotOpenNewWindow() {
        super("Cannot Open New Window ...");
    }
    public CannotOpenNewWindow(String message) {
        super(message);
    }
}
