package ch.admin.bar.siardsuite.component;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.geometry.Bounds;
import javafx.scene.control.Tooltip;

public class SiardToolip {

    private final MFXButton button;
    private final Tooltip tooltip;

    public SiardToolip(MFXButton button, Tooltip tooltip) {
        this.button = button;
        this.tooltip = tooltip;
    }

    public void setup() {
        button.setOnMouseMoved(event -> {
            Bounds boundsInScreen = button.localToScreen(button.getBoundsInLocal());
            tooltip.show(button,
                         (boundsInScreen.getMaxX() - boundsInScreen.getWidth() / 2) - tooltip.getWidth() / 2,
                         boundsInScreen.getMaxY() - boundsInScreen.getHeight() - tooltip.getHeight());
        });

        button.setOnMouseExited(event -> tooltip.hide());
    }
}
