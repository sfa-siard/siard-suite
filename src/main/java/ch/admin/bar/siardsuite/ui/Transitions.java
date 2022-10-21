package ch.admin.bar.siardsuite.ui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Transitions {

    ImageView imageView;

    public Transitions(ImageView imageView) {
        this.imageView = imageView;
    }

    public void rotate() {
        RotateTransition rt = new RotateTransition(Duration.millis(1000), this.imageView);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.play();
    }
}
