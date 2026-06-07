package de.soulfoxer.c3podebugger.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconFactory {
    private static final int DEFAULT_SIZE = 24;

    private IconFactory() {}

    public static ImageView create(String resourcePath) {
        return create(resourcePath, DEFAULT_SIZE);
    }

    public static ImageView create(String resourcePath, int size) {
        ImageView icon = new ImageView(new Image(
                IconFactory.class.getResourceAsStream(resourcePath)));
        icon.setFitWidth(size);
        icon.setFitHeight(size);
        icon.setPreserveRatio(true);
        return icon;
    }
}
