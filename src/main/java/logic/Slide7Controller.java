package logic;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;

public class Slide7Controller {

    @FXML
    private Button blockingButton;

    @FXML
    private Button startButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @FXML
    private Label stateLabel;

    @FXML
    private Slider responsivenessSlider;

    private Service<Void> worker;

    @FXML
    public void initialize() {
        createWorker();

        progressBar.setProgress(0);
        statusLabel.setText("Нажмите плохую кнопку — UI зависнет. Нажмите правильную — работа уйдёт в фон.");
        stateLabel.setText("Состояние Service: READY");

        cancelButton.setDisable(true);
    }

    @FXML
    private void startBlockingTask() {
        unbindWorkerProperties();

        blockingButton.setDisable(true);
        startButton.setDisable(true);
        cancelButton.setDisable(true);

        statusLabel.setText("Плохой запуск: тяжёлая работа выполняется прямо в UI-потоке...");
        stateLabel.setText("Состояние: UI THREAD BUSY");
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

      
        doBlockingWorkOnUiThread();

        progressBar.setProgress(1.0);
        statusLabel.setText("Готово. UI был заблокирован, поэтому кнопки не реагировали.");
        stateLabel.setText("Состояние: FINISHED, но ценой зависания интерфейса");

        blockingButton.setDisable(false);
        startButton.setDisable(false);
        cancelButton.setDisable(true);
    }

    @FXML
    private void startBackgroundTask() {
        if (worker != null && worker.isRunning()) {
            return;
        }

        createWorker();
        bindWorkerProperties();

        blockingButton.setDisable(true);
        startButton.setDisable(true);
        cancelButton.setDisable(false);

        worker.setOnSucceeded(event -> {
            unbindWorkerProperties();

            progressBar.setProgress(1.0);
            statusLabel.setText("Готово. UI не зависал, потому что работа выполнялась в фоне через Task / Service.");
            stateLabel.setText("Состояние Service: SUCCEEDED");

            blockingButton.setDisable(false);
            startButton.setDisable(false);
            cancelButton.setDisable(true);
        });

        worker.setOnCancelled(event -> {
            unbindWorkerProperties();

            progressBar.setProgress(0);
            statusLabel.setText("Task отменён. Интерфейс остался отзывчивым.");
            stateLabel.setText("Состояние Service: CANCELLED");

            blockingButton.setDisable(false);
            startButton.setDisable(false);
            cancelButton.setDisable(true);
        });

        worker.setOnFailed(event -> {
            unbindWorkerProperties();

            progressBar.setProgress(0);
            statusLabel.setText("Ошибка выполнения Task: " + getWorkerErrorMessage());
            stateLabel.setText("Состояние Service: FAILED");

            blockingButton.setDisable(false);
            startButton.setDisable(false);
            cancelButton.setDisable(true);
        });

        worker.restart();
    }

    @FXML
    private void cancelTask() {
        if (worker != null && worker.isRunning()) {
            worker.cancel();
        }
    }

    @FXML
    private void resetTask() {
        if (worker != null && worker.isRunning()) {
            worker.cancel();
        }

        unbindWorkerProperties();
        createWorker();

        progressBar.setProgress(0);
        statusLabel.setText("Нажмите плохую кнопку — UI зависнет. Нажмите правильную — работа уйдёт в фон.");
        stateLabel.setText("Состояние Service: READY");
     

        blockingButton.setDisable(false);
        startButton.setDisable(false);
        cancelButton.setDisable(true);
    }

    private void createWorker() {
        worker = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        updateProgress(0, 100);
                        updateMessage("Правильный запуск: тяжёлая работа выполняется в фоне...");

                        for (int i = 1; i <= 100; i++) {
                            if (isCancelled()) {
                                updateMessage("Задача отменяется...");
                                break;
                            }

                            Thread.sleep(50);

                            updateProgress(i, 100);
                            updateMessage("Выполнено: " + i + "%");
                        }

                        return null;
                    }
                };
            }
        };
    }

    private void bindWorkerProperties() {
        progressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();
        stateLabel.textProperty().unbind();

        progressBar.progressProperty().bind(worker.progressProperty());
        statusLabel.textProperty().bind(worker.messageProperty());
        stateLabel.textProperty().bind(worker.stateProperty().asString("Состояние Service: %s"));
    }

    private void unbindWorkerProperties() {
        progressBar.progressProperty().unbind();
        statusLabel.textProperty().unbind();
        stateLabel.textProperty().unbind();
    }

    private void doBlockingWorkOnUiThread() {
        try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private String getWorkerErrorMessage() {
        if (worker == null || worker.getException() == null) {
            return "неизвестная ошибка";
        }

        return worker.getException().getMessage();
    }
}