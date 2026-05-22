package logic;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class Slide6Controller {
    @FXML
    private TextField nameField;

    @FXML
    private Slider sizeSlider;

    @FXML
    private ColorPicker accentPicker;

    @FXML
    private Label previewLabel;

    @FXML
    private Label countLabel;

    @FXML
    private Label scaleLabel;

    @FXML
    public void initialize() {
        accentPicker.setValue(Color.web("#1b6ea8"));
        sizeSlider.setMin(0.8);
        sizeSlider.setMax(5);
        sizeSlider.setValue(1.0);

    

        countLabel.textProperty().bind(
                Bindings.format("Символов: %d", nameField.textProperty().length())
        );

        scaleLabel.textProperty().bind(
                Bindings.format("Масштаб preview: %.1fx", sizeSlider.valueProperty())
        );

        previewLabel.textProperty().bind(
        Bindings.when(nameField.textProperty().isEmpty())
                .then("Привет, JavaFX!")
                .otherwise(Bindings.concat("Привет, ", nameField.textProperty(), "!"))
        );

        previewLabel.textFillProperty().bind(accentPicker.valueProperty());
        previewLabel.scaleXProperty().bind(sizeSlider.valueProperty());
        previewLabel.scaleYProperty().bind(sizeSlider.valueProperty());
    }

    @FXML
    private void resetDemo() {
        nameField.clear();
        sizeSlider.setValue(1.0);
        accentPicker.setValue(Color.web("#1b6ea8"));
    }
}
