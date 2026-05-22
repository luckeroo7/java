package logic;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Slide11Controller {

    private static final int MAX_PARTICLES = 180;

    @FXML
    private Pane particlePane;

    @FXML
    private ToggleButton autoButton;

    @FXML
    private ProgressBar activityBar;

    @FXML
    private Label counterLabel;

    private final Random random = new Random();
    private final List<Particle> particles = new ArrayList<>();
    private Timeline engine;
    private Timeline autoEmitter;

    @FXML
    public void initialize() {
        engine = new Timeline(new KeyFrame(Duration.millis(32), event -> tick()));
        engine.setCycleCount(Animation.INDEFINITE);
        engine.play();

        autoEmitter = new Timeline(new KeyFrame(Duration.millis(480), event -> burst()));
        autoEmitter.setCycleCount(Animation.INDEFINITE);

        updateCounter();
    }

    @FXML
    private void burst() {
        double centerX = Math.max(120, particlePane.getWidth() / 2.0);
        double centerY = Math.max(90, particlePane.getHeight() / 2.0);

        for (int i = 0; i < 22; i++) {
            addParticle(centerX, centerY);
        }
        trimIfNeeded();
        updateCounter();
    }

    @FXML
    private void toggleAuto() {
        if (autoButton.isSelected()) {
            autoEmitter.play();
        } else {
            autoEmitter.stop();
        }
    }

    @FXML
    private void clear() {
        particles.clear();
        particlePane.getChildren().clear();
        updateCounter();
    }

    private void addParticle(double startX, double startY) {
        double radius = 4 + random.nextDouble() * 10;
        Circle circle = new Circle(radius);
        circle.setFill(Color.hsb(random.nextDouble() * 360, 0.55, 0.95, 0.88));
        circle.setCenterX(startX + random.nextDouble() * 40 - 20);
        circle.setCenterY(startY + random.nextDouble() * 30 - 15);

        double angle = random.nextDouble() * Math.PI * 2;
        double speed = 1.2 + random.nextDouble() * 5.2;
        Particle particle = new Particle(circle, Math.cos(angle) * speed, Math.sin(angle) * speed, 1.0);

        particles.add(particle);
        particlePane.getChildren().add(circle);
    }

    private void tick() {
        double width = Math.max(200, particlePane.getWidth());
        double height = Math.max(180, particlePane.getHeight());

        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            Circle circle = particle.circle();

            circle.setCenterX(circle.getCenterX() + particle.vx());
            circle.setCenterY(circle.getCenterY() + particle.vy());
            circle.setOpacity(Math.max(0, circle.getOpacity() - 0.008));
            circle.setRadius(Math.max(0.4, circle.getRadius() * 0.992));

            boolean outside = circle.getCenterX() < -40 || circle.getCenterX() > width + 40
                    || circle.getCenterY() < -40 || circle.getCenterY() > height + 40;
            boolean invisible = circle.getOpacity() <= 0.05;

            if (outside || invisible) {
                particlePane.getChildren().remove(circle);
                iterator.remove();
            }
        }

        updateCounter();
    }

    private void trimIfNeeded() {
        while (particles.size() > MAX_PARTICLES) {
            Particle oldest = particles.remove(0);
            particlePane.getChildren().remove(oldest.circle());
        }
    }

    private void updateCounter() {
        int count = particles.size();
        counterLabel.setText("Частиц: " + count + " / " + MAX_PARTICLES);
        activityBar.setProgress(count / (double) MAX_PARTICLES);
    }

    private record Particle(Circle circle, double vx, double vy, double life) {
    }
}
