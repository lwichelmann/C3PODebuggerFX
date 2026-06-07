package de.soulfoxer.c3podebugger.util;

import javafx.scene.control.Button;

public class ButtonStyler {
    private ButtonStyler() {}

    public static void applyToolbarStyle(Button button) {
        button.setStyle(Styling.BUTTON);
        button.setOnMouseEntered(event -> button.setStyle(Styling.BUTTON_HOVER));
        button.setOnMouseExited(event -> button.setStyle(Styling.BUTTON));
    }
}
