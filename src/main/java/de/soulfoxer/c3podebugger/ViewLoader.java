package de.soulfoxer.c3podebugger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public final class ViewLoader {

    public static Parent load(Class<?> controllerClass) throws IOException {
        String name = controllerClass.getSimpleName().replace("Controller", "View");
        String fxml = name + ".fxml";

        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(fxml));
        return loader.load();
    }
}