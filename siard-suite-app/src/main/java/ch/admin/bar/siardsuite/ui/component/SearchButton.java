package ch.admin.bar.siardsuite.ui.component;

import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class SearchButton extends MFXButton {
    public SearchButton(final EventHandler<ActionEvent> onActionListener) {
        I18n.bind(this.textProperty(), "search.metadata.dialog.search");
        this.getStyleClass().add("primary");
        this.setOnAction(onActionListener);
    }
}
