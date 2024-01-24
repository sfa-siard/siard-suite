package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public class SiardTooltip extends Tooltip {

    @Deprecated
    public SiardTooltip(String messageKey) {
        this(DisplayableText.of(I18nKey.of(messageKey)));
    }

    public SiardTooltip(DisplayableText displayableText) {
        super();
        this.setPrefSize(328.0, 162);
        this.setAutoHide(true);
        this.getStyleClass().add("info-tooltip");
        this.textProperty().bind(displayableText.bindable());
    }

    public void showOnMouseOn(final Node node) {
        node.setOnMouseMoved(event -> {
            Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());
            this.show(node,
                    (boundsInScreen.getMaxX() - boundsInScreen.getWidth() / 2) - this.getWidth() / 2,
                    boundsInScreen.getMaxY() - boundsInScreen.getHeight() - this.getHeight());
        });

        node.setOnMouseExited(event -> this.hide());
    }
}
