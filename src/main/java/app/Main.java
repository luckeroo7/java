package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import logic.MainController;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/resources/main.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 720);
        MainController controller = loader.getController();

        stage.setTitle("Пара слов о JavaFx");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(540);
        stage.setFullScreenExitHint("");

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> handleKey(stage, controller, event));
        stage.show();
    }

    private void handleKey(Stage stage, MainController controller, KeyEvent event) {
        KeyCode code = event.getCode();

        switch (code) {
            case PAGE_DOWN:
                controller.nextSlide();
                event.consume();
                break;
            case PAGE_UP:
                controller.prevSlide();
                event.consume();
                break;
            case F11:
                stage.setFullScreen(!stage.isFullScreen());
                event.consume();
                break;
            case ESCAPE:
                if (stage.isFullScreen()) {
                    stage.setFullScreen(false);
                    event.consume();
                }
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
