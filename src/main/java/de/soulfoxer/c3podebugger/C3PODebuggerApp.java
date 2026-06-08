package de.soulfoxer.c3podebugger;

import de.soulfoxer.c3podebugger.util.IconPaths;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class C3PODebuggerApp extends Application {

    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 750;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(ViewLoader.load(MainFragmentController.class), WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setTitle("C3PO Debugger");
        stage.setResizable(false);
        setAppIcon(stage);
        stage.setScene(scene);
        stage.show();
    }

    private void setAppIcon(Stage stage) {
        stage.getIcons().add(new Image(getClass().getResourceAsStream(IconPaths.APP_ICON)));
    }
}
