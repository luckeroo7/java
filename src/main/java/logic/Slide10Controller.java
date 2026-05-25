package logic;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

public class Slide10Controller {

    @FXML
    private ColorPicker fillPicker;

    @FXML
    private Slider arcSlider;

    @FXML
    private Slider rotateSlider;

    @FXML
    private Slider shadowSlider;

    @FXML
    private Rectangle card;

    @FXML
    private Label statusLabel;

    private final Random random = new Random();

    @FXML
    public void initialize() {
        arcSlider.setMin(0);
        arcSlider.setMax(120);
        arcSlider.setValue(42);

        rotateSlider.setMin(-25);
        rotateSlider.setMax(25);
        rotateSlider.setValue(0);

        shadowSlider.setMin(0);
        shadowSlider.setMax(60);
        shadowSlider.setValue(24);

        fillPicker.setValue(Color.web("#9ed8ff"));

        card.fillProperty().bind(fillPicker.valueProperty());
        card.arcWidthProperty().bind(arcSlider.valueProperty());
        card.arcHeightProperty().bind(arcSlider.valueProperty());
        card.rotateProperty().bind(rotateSlider.valueProperty());

        shadowSlider.valueProperty().addListener((observable, oldValue, newValue) -> applyShadow(newValue.doubleValue()));
        applyShadow(shadowSlider.getValue());

        statusLabel.textProperty().bind(javafx.beans.binding.Bindings.format(
                "fill=%s | arc=%.0f | rotate=%.0f° | shadow=%.0f",
                fillPicker.valueProperty().asString(),
                arcSlider.valueProperty(),
                rotateSlider.valueProperty(),
                shadowSlider.valueProperty()
        ));
    }

    @FXML
    private void randomize() {
        fillPicker.setValue(Color.hsb(random.nextDouble() * 360, 0.45 + random.nextDouble() * 0.35, 0.95));
        arcSlider.setValue(10 + random.nextDouble() * 100);
        rotateSlider.setValue(-22 + random.nextDouble() * 44);
        shadowSlider.setValue(8 + random.nextDouble() * 52);
        pulse();
    }

    @FXML
    private void reset() {
        fillPicker.setValue(Color.web("#9ed8ff"));
        arcSlider.setValue(42);
        rotateSlider.setValue(0);
        shadowSlider.setValue(24);
        pulse();
    }

    private void applyShadow(double radius) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(radius);
        shadow.setOffsetY(Math.max(2, radius / 4));
        shadow.setColor(Color.rgb(31, 45, 58, 0.28));
        card.setEffect(shadow);
    }

    private void pulse() {
        FadeTransition transition = new FadeTransition(Duration.millis(160), card);
        transition.setFromValue(0.55);
        transition.setToValue(1.0);
        transition.play();
    }
}
