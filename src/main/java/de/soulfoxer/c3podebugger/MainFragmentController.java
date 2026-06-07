package de.soulfoxer.c3podebugger;

import de.soulfoxer.c3podebugger.util.InjectionChecker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainFragmentController {

    @FXML private Button playButton;
    @FXML private Button stopButton;
    @FXML private Button resumeButton;
    @FXML private Button stepOverButton;

    @FXML
    private void initialize() {
        InjectionChecker.assertInjection(this);

        playButton.setGraphic(icon("icons/play.png"));
        stopButton.setGraphic(icon("icons/stop.png"));
        resumeButton.setGraphic(icon("icons/resume.png"));
        stepOverButton.setGraphic(icon("icons/stepover.png"));

        playButton.setOnAction(e -> onPlay());
        stopButton.setOnAction(e -> onStop());
        resumeButton.setOnAction(e -> onResume());
        stepOverButton.setOnAction(e -> onStepOver());
    }

    private ImageView icon(String path) {
        ImageView view = new ImageView(new Image(getClass().getResourceAsStream(path)));
        view.setFitWidth(16);
        view.setFitHeight(16);
        view.setPreserveRatio(true);
        return view;
    }

    private void onPlay()     { System.out.println("Play"); }
    private void onStop()     { System.out.println("Stop"); }
    private void onResume()   { System.out.println("Resume"); }
    private void onStepOver() { System.out.println("Step over"); }
}