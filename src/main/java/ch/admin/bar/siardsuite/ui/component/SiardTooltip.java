package ch.admin.bar.siardsuite.ui.component;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public class SiardTooltip extends Tooltip {

    public SiardTooltip(DisplayableText displayableText) {
        super();
        this.setPrefSize(328.0, 162);
        this.setAutoHide(true);
        this.getStyleClass().add("info-tooltip");
        this.textProperty().bind(displayableText.bindable());
    }

    public SiardTooltip(I18nKey i18nKey) {
        this(DisplayableText.of(i18nKey));
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
