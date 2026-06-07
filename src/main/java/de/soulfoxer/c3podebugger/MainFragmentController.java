package de.soulfoxer.c3podebugger;

import de.soulfoxer.c3podebugger.util.InjectionChecker;
import de.soulfoxer.c3podebugger.util.Styling;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class MainFragmentController {

    private static final int ICON_SIZE = 24;

    private static final String PLAY_ICON_PATH = "icons/play.png";
    private static final String STOP_ICON_PATH = "icons/stop.png";
    private static final String RESUME_ICON_PATH = "icons/resume.png";
    private static final String STEP_OVER_ICON_PATH = "icons/stepover.png";

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
    private void initialize() {
        validateInjection();
        initializeLayout();
        initializeButtons();
    }

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
        styleButton(playButton);
        styleButton(stopButton);
        styleButton(resumeButton);
        styleButton(stepOverButton);
    }

    private void styleButton(Button button) {
        button.setStyle(Styling.BUTTON);
        button.setOnMouseEntered(event -> applyButtonHoverStyle(button));
        button.setOnMouseExited(event -> applyButtonDefaultStyle(button));
    }

    private void applyButtonHoverStyle(Button button) {
        button.setStyle(Styling.BUTTON_HOVER);
    }

    private void applyButtonDefaultStyle(Button button) {
        button.setStyle(Styling.BUTTON);
    }

    private void setButtonIcons() {
        setButtonIcon(playButton, PLAY_ICON_PATH);
        setButtonIcon(stopButton, STOP_ICON_PATH);
        setButtonIcon(resumeButton, RESUME_ICON_PATH);
        setButtonIcon(stepOverButton, STEP_OVER_ICON_PATH);
    }

    private void setButtonIcon(Button button, String iconPath) {
        button.setGraphic(createIcon(iconPath));
    }

    private ImageView createIcon(String path) {
        ImageView icon = new ImageView(loadImage(path));
        configureIcon(icon);
        return icon;
    }

    private Image loadImage(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    private void configureIcon(ImageView icon) {
        icon.setFitWidth(ICON_SIZE);
        icon.setFitHeight(ICON_SIZE);
        icon.setPreserveRatio(true);
    }

    private void registerButtonActions() {
        playButton.setOnAction(event -> onPlay());
        stopButton.setOnAction(event -> onStop());
        resumeButton.setOnAction(event -> onResume());
        stepOverButton.setOnAction(event -> onStepOver());
    }

    private void onPlay() {
        System.out.println("Play");
    }

    private void onStop() {
        System.out.println("Stop");
    }

    private void onResume() {
        System.out.println("Resume");
    }

    private void onStepOver() {
        System.out.println("Step over");
    }
}