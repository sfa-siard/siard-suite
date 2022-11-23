package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;

// understands a button that can close a dialog...
public class DialogButton extends MFXButton {

    public DialogButton(Boolean primaryStyle, String textKey) {
        I18n.bind(this.textProperty(),textKey);
        this.getStyleClass().setAll("button", primaryStyle ? "primary": "secondary");
        this.setManaged(true);
    }
}
