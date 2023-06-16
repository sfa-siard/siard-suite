package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;

// understands a button that can close a dialog...
public class CloseDialogButton extends MFXButton {

    public CloseDialogButton(RootStage rootStage) {
        I18n.bind(this.textProperty(),"button.cancel");
        this.getStyleClass().setAll("button", "secondary");
        this.setManaged(true);
        this.setOnAction(event -> rootStage.closeDialog());
    }
}
