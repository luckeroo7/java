package logic;

import java.util.function.DoubleUnaryOperator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class Slide9Controller {

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Label formulaLabel;

    private Timeline animation;
    private double animatedX;

    @FXML
    public void initialize() {
        lineChart.setAnimated(false);
        showQuadratic();
    }

    @FXML
    private void showQuadratic() {
        stopAnimation();
        xAxis.setLowerBound(-10);
        xAxis.setUpperBound(10);
        xAxis.setTickUnit(2);
        yAxis.setLowerBound(-10);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);
        formulaLabel.setText("Текущая функция: y = x²");
        plotFunction("y = x²", x -> x * x, -10, 10, 0.25);
    }

    @FXML
    private void showSine() {
        stopAnimation();
        xAxis.setLowerBound(-10);
        xAxis.setUpperBound(10);
        xAxis.setTickUnit(2);
        yAxis.setLowerBound(-1.5);
        yAxis.setUpperBound(1.5);
        yAxis.setTickUnit(0.5);
        formulaLabel.setText("Текущая функция: y = sin(x)");
        plotFunction("y = sin(x)", Math::sin, -10, 10, 0.1);
    }

    @FXML
    private void showCosine() {
        stopAnimation();
        xAxis.setLowerBound(-10);
        xAxis.setUpperBound(10);
        xAxis.setTickUnit(2);
        yAxis.setLowerBound(-1.5);
        yAxis.setUpperBound(1.5);
        yAxis.setTickUnit(0.5);
        formulaLabel.setText("Текущая функция: y = cos(x)");
        plotFunction("y = cos(x)", Math::cos, -10, 10, 0.1);
    }

    @FXML
    private void startStreamingDemo() {
        stopAnimation();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(12);
        xAxis.setTickUnit(1);
        yAxis.setLowerBound(-1.5);
        yAxis.setUpperBound(1.5);
        yAxis.setTickUnit(0.5);
        formulaLabel.setText("Timeline: точки добавляются постепенно, график обновляется сам");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("stream sin(x)");
        lineChart.getData().setAll(series);
        animatedX = 0;

        animation = new Timeline(new KeyFrame(Duration.millis(80), event -> {
            series.getData().add(new XYChart.Data<>(animatedX, Math.sin(animatedX)));
            animatedX += 0.2;
            if (animatedX > 12) {
                animation.stop();
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void plotFunction(String seriesName, DoubleUnaryOperator evaluator, double start, double end, double step) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        for (double x = start; x <= end; x += step) {
            series.getData().add(new XYChart.Data<>(x, evaluator.applyAsDouble(x)));
        }

        lineChart.getData().setAll(series);
    }

    private void stopAnimation() {
        if (animation != null) {
            animation.stop();
        }
    }
}
