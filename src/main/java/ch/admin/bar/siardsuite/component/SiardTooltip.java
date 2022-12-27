package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.util.I18n;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class SiardTooltip extends Tooltip {

    public SiardTooltip(String messageKey) {
        super();
        this.setPrefSize(328.0, 162);
        // TODO
        //this.setShowDelay(Duration.millis(1));
        this.setAutoHide(true);
        this.getStyleClass().add("info-tooltip");
        this.textProperty().bind(I18n.createStringBinding(messageKey));
    }
}
