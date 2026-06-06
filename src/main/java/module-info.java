module de.soulfoxer.c3podebugger {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens de.soulfoxer.c3podebugger to javafx.fxml;
    exports de.soulfoxer.c3podebugger;
}