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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final Set<Integer> activeBreakpoints = new HashSet<>();
    private final List<DebugVariable> globalVars = new ArrayList<>();
    private final List<DebugVariable> localVars = new ArrayList<>();
    private final DebugConnection connection = new DebugConnection(this);

    private int highlightedStart = -1;
    private int highlightedEnd = -1;
    private int activeHighlightedLine = -1;

    @FXML
    private ListView<Integer> gutterView;

    @FXML
    private void initialize() {
        validateInjection();
        initializeLayout();
        initializeButtons();
        initializeStatus();

        loadTestData();
        initializeCodeArea();
        setupColumnNumberBar();
    }

    private void setupColumnNumberBar() {
        gutterView.setCellFactory(listView -> new ListCell<>() {
            private final ImageView breakpointIcon = IconFactory.create("/de/soulfoxer/c3podebugger/icons/breakpoint.png", 12);

            @Override
            protected void updateItem(Integer lineNumber, boolean empty) {
                super.updateItem(lineNumber, empty);
                if (empty || lineNumber == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.valueOf(lineNumber));
                    setStyle("-fx-text-fill: #606366; -fx-alignment: center-right; -fx-font-family: monospace; -fx-background-color: #2b2b2b;");

                    setGraphic(activeBreakpoints.contains(lineNumber) ? breakpointIcon : null);

                    setOnMouseClicked(event -> {
                        if (activeBreakpoints.contains(lineNumber)) {
                            activeBreakpoints.remove(lineNumber);
                        } else {
                            activeBreakpoints.add(lineNumber);
                        }
                        getListView().refresh();
                    });
                }
            }
        });

        codeArea.scrollTopProperty().addListener((obs, oldVal, newVal) -> {
            int topIndex = (int) (newVal.doubleValue() / 15.0);
            gutterView.scrollTo(topIndex);
        });
    }

    private void initializeCodeArea() {
        codeArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (highlightedStart != -1 && highlightedEnd != -1) {
                Platform.runLater(() -> codeArea.selectRange(highlightedStart, highlightedEnd));
            }
        });
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
            activeHighlightedLine = lineNumber;
            int caretPosition = 0;
            for (int i = 0; i < lineNumber - 1; i++) {
                caretPosition += lines[i].length() + 1;
            }

            highlightedStart = caretPosition;
            highlightedEnd = caretPosition + lines[lineNumber - 1].length();

            Platform.runLater(() -> {
                codeArea.selectRange(highlightedStart, highlightedEnd);
            });
        }
    }


    private void handlePausedState(String line) {
        globalVars.clear();
        localVars.clear();

        try {
            int lineNumber = Integer.parseInt(line.split("=")[1]);
            highlightCodeLine(lineNumber);
            setStepControlsDisabled(false);
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

        globalVars.clear();
        globalVars.add(new DebugVariable("PI", "3.14159", "float", true));
        globalVars.add(new DebugVariable("MAX_BUFFER", "1024", "int", true));
        globalVars.add(new DebugVariable("DEBUG_MODE", "true", "bool", true));
        updateVariablesTree(globalVariablesTree, globalVars);

        localVars.clear();
        localVars.add(new DebugVariable("x", "10", "int", false));
        localVars.add(new DebugVariable("y", "32", "int", false));
        localVars.add(new DebugVariable("result", "42", "int", false));
        updateVariablesTree(localVariablesTree, localVars);


        Platform.runLater(() -> {
            highlightCodeLine(11);
        });

        int lineCount = sampleCode.split("\n").length;
        gutterView.getItems().clear();
        for (int i = 1; i <= lineCount; i++) {
            gutterView.getItems().add(i);
        }
    }
}
