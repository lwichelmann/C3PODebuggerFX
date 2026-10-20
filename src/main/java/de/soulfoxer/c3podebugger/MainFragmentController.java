package de.soulfoxer.c3podebugger;

import de.soulfoxer.c3podebugger.connection.ConnectionListener;
import de.soulfoxer.c3podebugger.connection.ConnectionState;
import de.soulfoxer.c3podebugger.connection.DebugConnection;
import de.soulfoxer.c3podebugger.model.DebugVariable;
import de.soulfoxer.c3podebugger.util.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

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
    private TextArea codeArea;

    @FXML
    private TreeView<String> globalVariablesTree;

    @FXML
    private TreeView<String> localVariablesTree;

    @FXML
    private TextArea consoleArea;

    private final List<DebugVariable> globalVars = new ArrayList<>();
    private final List<DebugVariable> localVars = new ArrayList<>();
    private final DebugConnection connection = new DebugConnection(this);

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
            case CONNECTED -> IconPaths.DOT_CONNECTED;
            case CONNECTING -> IconPaths.DOT_CONNECTING;
            case DISCONNECTED -> IconPaths.DOT_DISCONNECTED;
        };
        statusDot.setImage(new Image(getClass().getResourceAsStream(path)));
    }

    private void updateStatusLabel(ConnectionState state) {
        String text = switch (state) {
            case CONNECTED -> "verbunden";
            case CONNECTING -> "verbinde…";
            case DISCONNECTED -> "nicht verbunden";
        };
        statusLabel.setText(text);
    }

    @Override
    public void onMessage(String line) {
        Platform.runLater(() -> {
            if (line.startsWith("PAUSED:")) {
                handlePausedState(line);
            } else if (line.startsWith("VAR:")) {
                handleVariableUpdate(line);
            } else if (line.startsWith("CODE:")) {
                codeArea.setText(line.substring(5));
            } else {
                consoleArea.appendText(line + "\n");
            }
        });
    }

    private void handleVariableUpdate(String line) {
        // Format: VAR:GLOBAL:name=value:type
        String body = line.substring(4);
        int firstColon = body.indexOf(':');
        int lastColon = body.lastIndexOf(':');
        int equalsSign = body.indexOf('=', firstColon + 1);

        if (firstColon < 0 || lastColon <= firstColon || equalsSign < 0 || equalsSign > lastColon) {
            consoleArea.appendText("[WARN] Ungültige Variablenzeile: " + line + "\n");
            return;
        }

        boolean isGlobal = "GLOBAL".equalsIgnoreCase(body.substring(0, firstColon));
        String name = body.substring(firstColon + 1, equalsSign);
        String value = body.substring(equalsSign + 1, lastColon);
        String type = body.substring(lastColon + 1);

        DebugVariable variable = new DebugVariable(name, value, type, isGlobal);

        if (isGlobal) {
            globalVars.add(variable);
            updateVariablesTree(globalVariablesTree, globalVars);
        } else {
            localVars.add(variable);
            updateVariablesTree(localVariablesTree, localVars);
        }
    }

    private void highlightCodeLine(int lineNumber) {
        String text = codeArea.getText().replace("\r\n", "\n");
        String[] lines = text.split("\n");

        if (lineNumber > 0 && lineNumber <= lines.length) {
            int caretPosition = 0;
            for (int i = 0; i < lineNumber - 1; i++) {
                caretPosition += lines[i].length() + 1;
            }
            codeArea.selectRange(caretPosition, caretPosition + lines[lineNumber - 1].length());
            codeArea.requestFocus(); // Fokus erzwingen, damit Markierung sichtbar wird
        }
    }


    private void handlePausedState(String line) {
        // Beim Pausieren alte Variablen leeren
        globalVars.clear();
        localVars.clear();

        try {
            int lineNumber = Integer.parseInt(line.split("=")[1]);
            highlightCodeLine(lineNumber);
            setStepControlsDisabled(false); // Buttons freigeben
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            consoleArea.appendText("[WARN] Fehler beim Parsen der Breakpoint-Zeile: " + line + "\n");
        }
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
        setStepControlsDisabled(true);
    }

    private void setStepControlsDisabled(boolean b) {
        stepOverButton.setDisable(b);
        resumeButton.setDisable(b);
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

    private void updateVariablesTree(TreeView<String> treeView, List<DebugVariable> variables) {
        TreeItem<String> rootNode = new TreeItem<>("Variables");
        rootNode.setExpanded(true);

        for (DebugVariable var : variables) {
            TreeItem<String> item = new TreeItem<>(var.name() + " = " + var.value() + " (" + var.type() + ")");
            rootNode.getChildren().add(item);
        }

        treeView.setRoot(rootNode);
        treeView.setShowRoot(false);
    }

    public void loadTestData() {
        String sampleCode = """
                // C3PO Test-Skript fir Debugger
                import math;
                
                fn calculateSum(a: int, b: int) -> int {
                    let temp = a + b;
                    return temp;
                }
                
                fn main() {
                    let x = 10;
                    let y = 32;
                    let result = calculateSum(x, y);
                    print(result);
                }
                """;
        codeArea.setText(sampleCode);

        consoleArea.clear();
        consoleArea.appendText("[DEBUG] Verbindung zu c3po.exe opgebaut (Port 5005)\n");
        consoleArea.appendText("[DEBUG] Haltepunkt an Zeil 11 erreecht.\n");
        consoleArea.appendText("[INFO] Starten Skriptausféierung...\n");

        updateVariablesTree(globalVariablesTree, List.of(
                new DebugVariable("PI", "3.14159", "float", true),
                new DebugVariable("MAX_BUFFER", "1024", "int", true),
                new DebugVariable("DEBUG_MODE", "true", "bool", true)));

        updateVariablesTree(localVariablesTree, List.of(
                new DebugVariable("x", "10", "int", false),
                new DebugVariable("y", "32", "int", false),
                new DebugVariable("result", "42", "int", false)));

        highlightCodeLine(11);
    }
}
