package de.soulfoxer.c3podebugger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;

public final class ViewLoader {

    private ViewLoader() {
    }

    public static Parent load(Class<?> controllerClass) throws IOException {
        String controllerName = controllerClass.getSimpleName();

        if (!controllerName.endsWith("Controller")) {
            throw new IOException("Could not load view: Controller class name must end with 'Controller': " + controllerName);
        }

        String viewName = controllerName.replaceFirst("Controller$", "View");
        String fxml = viewName + ".fxml";

        URL fxmlUrl = controllerClass.getResource(fxml);

        if (fxmlUrl == null) {
            throw new IOException("Could not load view: " + fxml);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        try {
            return loader.load();
        }catch(IOException e) {
            throw new IOException("Could not load view: " + fxml, e);
        }

    }
}