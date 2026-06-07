package de.soulfoxer.c3podebugger.util;

public class Styling {
    private Styling() {
    }

    public static final String ROOT =
            "-fx-background-color: #2b2b2b;";

    public static final String TOOLBAR =
            "-fx-background-color: #3c3f41;" +
                    "-fx-padding: 4 8 4 8;" +
                    "-fx-spacing: 4;" +
                    "-fx-alignment: center-left;";

    public static final String SPACER =
            "-fx-background-color: transparent;";

    public static final String DIVIDER =
            "-fx-min-width: 1;" +
                    "-fx-max-width: 1;" +
                    "-fx-background-color: #555555;";

    public static final String BUTTON =
            "-fx-background-color: transparent;" +
                    "-fx-background-radius: 4;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 4;";

    public static final String BUTTON_HOVER =
            "-fx-background-color: #4e5254;" +
                    "-fx-background-radius: 4;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 4;";
}

