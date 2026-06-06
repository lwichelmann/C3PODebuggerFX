package de.soulfoxer.c3podebugger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class C3PODebuggerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(ViewLoader.load(MainFragmentController.class), 320, 240);
        stage.setTitle("C3PO Debugger");
        stage.setScene(scene);
        stage.show();
    }
}
