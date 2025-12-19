package ch.admin.bar.siardsuite.ui.animations;

import javafx.animation.PathTransition;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class Animation {
    private final PathTransition transition;

    public Animation(PathTransition transition) {
        this.transition = transition;
    }

    public void start(ImageView path, ImageView bubble) {
        Bounds bounds = path.localToScreen(path.getBoundsInLocal());
        bubble.setVisible(true);
        Line line = new Line(0, bounds.getHeight() / 2 - 10, bounds.getWidth() - 25, bounds.getHeight() / 2 - 10);
        transition.setNode(bubble);
        transition.setDuration(Duration.seconds(1));
        transition.setPath(line);
        transition.setCycleCount(javafx.animation.Animation.INDEFINITE);
        transition.play();
    }

    public void stop() {
        transition.stop();
    }
}
