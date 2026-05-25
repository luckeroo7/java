module javafx.demo.deck {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    exports app;
    opens logic to javafx.fxml;
}
