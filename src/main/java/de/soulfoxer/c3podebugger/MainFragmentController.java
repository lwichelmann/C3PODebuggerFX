package de.soulfoxer.c3podebugger;

import de.soulfoxer.c3podebugger.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class MainFragmentController {

    private static final int ICON_SIZE = 24;

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