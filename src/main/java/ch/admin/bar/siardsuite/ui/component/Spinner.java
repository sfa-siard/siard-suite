package ch.admin.bar.siardsuite.ui.component;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Spinner {

    final ImageView imageView;
    final RotateTransition transition;

    public Spinner(ImageView imageView) {
        this.imageView = imageView;
        transition = new RotateTransition(Duration.millis(1000), this.imageView);
        transition.setByAngle(360);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setInterpolator(Interpolator.LINEAR);
    }

    public void play() {
        this.transition.play();
    }

    public void hide() {
        this.transition.stop();
        this.imageView.setVisible(false);
    }
}
