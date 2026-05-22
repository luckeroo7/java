package logic;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MainController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Label slideLabel;

    @FXML
    private Label topicLabel;

    @FXML
    private ProgressBar deckProgress;

    private int currentSlide = 0;

    private final String[] slides = {
            "/resources/slide1.fxml",
            "/resources/slide2.fxml",
            "/resources/slide3.fxml",
            "/resources/slide4.fxml",
            "/resources/slide5.fxml",
            "/resources/slide6.fxml",
            "/resources/slide7.fxml",
            "/resources/slide8.fxml",
            "/resources/slide9.fxml",
            "/resources/slide10.fxml",
            "/resources/slide11.fxml",
            "/resources/slide12.fxml"
    };

    private final String[] topics = {
            "Старт",
            "Где уместен JavaFX",
            "Модули платформы",
            "Scene Graph",
            "FXML + CSS + Controller",
            "Binding Playground",
            "Task / Service",
            "Observable Collections",
            "Charts + Animation",
            "CSS + Effects Lab",
            "Particle Timeline",
            "Итоги и вопросы"
    };

    @FXML
    public void initialize() {
        loadSlide(currentSlide, 0);
    }

    public void nextSlide() {
        if (currentSlide < slides.length - 1) {
            currentSlide++;
            loadSlide(currentSlide, 1);
        }
    }

    public void prevSlide() {
        if (currentSlide > 0) {
            currentSlide--;
            loadSlide(currentSlide, -1);
        }
    }

    public void firstSlide() {
        currentSlide = 0;
        loadSlide(currentSlide, -1);
    }

    public void lastSlide() {
        currentSlide = slides.length - 1;
        loadSlide(currentSlide, 1);
    }

    private void loadSlide(int index, int direction) {
        try {
            Parent slide = FXMLLoader.load(getClass().getResource(slides[index]));
            applyResponsiveFonts(slide);
            contentPane.getChildren().setAll(slide);
            animateSlide(slide, direction);
            updateChrome();
        } catch (Exception e) {
            e.printStackTrace();
            slideLabel.setText("Ошибка загрузки слайда " + (index + 1));
            topicLabel.setText(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private void animateSlide(Parent slide, int direction) {
        slide.setOpacity(0);
        slide.setTranslateX(direction == 0 ? 0 : direction * 50);

        FadeTransition fade = new FadeTransition(Duration.millis(220), slide);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition translate = new TranslateTransition(Duration.millis(220), slide);
        translate.setFromX(slide.getTranslateX());
        translate.setToX(0);

        new ParallelTransition(fade, translate).play();
    }

    private void updateChrome() {
        slideLabel.setText((currentSlide + 1) + " / " + slides.length);
        topicLabel.setText(topics[currentSlide]);
        deckProgress.setProgress((currentSlide + 1.0) / slides.length);
    }

    private void bindFont(Labeled node, double factor) {
        node.styleProperty().bind(Bindings.concat("-fx-font-size: ",
                contentPane.widthProperty().multiply(factor).asString("%.0f"),
                "px;"));
    }

    private void applyResponsiveFonts(Parent root) {
        root.lookupAll(".label-title").forEach(node -> bindFont((Labeled) node, 0.047));
        root.lookupAll(".label-subtitle").forEach(node -> bindFont((Labeled) node, 0.034));
        root.lookupAll(".label-content").forEach(node -> bindFont((Labeled) node, 0.026));
        root.lookupAll(".label-hint").forEach(node -> bindFont((Labeled) node, 0.023));
        root.lookupAll(".label-code").forEach(node -> bindFont((Labeled) node, 0.019));
        root.lookupAll(".label-mini").forEach(node -> bindFont((Labeled) node, 0.017));
    }
}
