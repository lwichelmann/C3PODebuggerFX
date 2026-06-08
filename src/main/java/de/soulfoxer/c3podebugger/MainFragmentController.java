package de.soulfoxer.c3podebugger;

import de.soulfoxer.c3podebugger.connection.ConnectionListener;
import de.soulfoxer.c3podebugger.connection.ConnectionState;
import de.soulfoxer.c3podebugger.connection.DebugConnection;
import de.soulfoxer.c3podebugger.util.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class MainFragmentController implements ConnectionListener {

    @FXML
    private BorderPane root;
    @FXML
    private HBox toolBar;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button resumeButton;
    @FXML
    private Button stepOverButton;
    @FXML
    private Region spacer;
    @FXML
    private Region divider;
    @FXML
    private ImageView statusDot;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        validateInjection();
        initializeLayout();
        initializeButtons();
        initializeStatus();
    }

    private void initializeStatus() {
        statusLabel.setStyle(Styling.STATUS_LABEL);
        updateStatus(connection.getState());
    }

    @Override
    public void onStateChanged(ConnectionState state) {
        Platform.runLater(() -> updateStatus(state));
    }

    private void updateStatus(ConnectionState state) {
        updateStatusDot(state);
        updateStatusLabel(state);
    }

    private void updateStatusDot(ConnectionState state) {
        String path = switch (state) {
            case CONNECTED    -> IconPaths.DOT_CONNECTED;
            case CONNECTING   -> IconPaths.DOT_CONNECTING;
            case DISCONNECTED -> IconPaths.DOT_DISCONNECTED;
        };
        statusDot.setImage(new Image(getClass().getResourceAsStream(path)));
    }

    private void updateStatusLabel(ConnectionState state) {
        String text = switch (state) {
            case CONNECTED    -> "verbunden";
            case CONNECTING   -> "verbinde…";
            case DISCONNECTED -> "nicht verbunden";
        };
        statusLabel.setText(text);
    }

    @Override
    public void onMessage(String line) {
        Platform.runLater(() -> System.out.println("vom Interpreter: " + line));
    }


    private final DebugConnection connection = new DebugConnection(this);

    private void validateInjection() {
        InjectionChecker.assertInjection(this);
    }

    private void initializeLayout() {
        styleRoot();
        styleToolBar();
        styleSpacer();
        styleDivider();
    }

    private void styleDivider() {
        divider.setStyle(Styling.DIVIDER);
    }

    private void styleSpacer() {
        spacer.setStyle(Styling.SPACER);
    }

    private void styleRoot() {
        root.setStyle(Styling.ROOT);
    }

    private void styleToolBar() {
        toolBar.setStyle(Styling.TOOLBAR);
    }

    private void initializeButtons() {
        styleButtons();
        setButtonIcons();
        registerButtonActions();
    }

    private void styleButtons() {
        ButtonStyler.applyToolbarStyle(playButton);
        ButtonStyler.applyToolbarStyle(stopButton);
        ButtonStyler.applyToolbarStyle(resumeButton);
        ButtonStyler.applyToolbarStyle(stepOverButton);
    }

    private void setButtonIcons() {
        playButton.setGraphic(IconFactory.create(IconPaths.PLAY));
        stopButton.setGraphic(IconFactory.create(IconPaths.STOP));
        resumeButton.setGraphic(IconFactory.create(IconPaths.RESUME));
        stepOverButton.setGraphic(IconFactory.create(IconPaths.STEP_OVER));
    }

    private void registerButtonActions() {
        playButton.setOnAction(event -> onPlay());
        stopButton.setOnAction(event -> onStop());
        resumeButton.setOnAction(event -> onResume());
        stepOverButton.setOnAction(event -> onStepOver());
    }

    private void onPlay() {
        // blackhole IP (e.g. 10.255.255.1) makes connect hang until timeout -> handy to test the ORANGE state
        connection.connect("127.0.0.1", 5005);
    }

    private void onStop() {

        connection.disconnect();
    }

    private void onResume() {
        System.out.println("Resume");
    }

    private void onStepOver() {
        System.out.println("Step over");
    }
}