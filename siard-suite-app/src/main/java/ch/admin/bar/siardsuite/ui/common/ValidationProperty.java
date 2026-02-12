package ch.admin.bar.siardsuite.ui.common;

import ch.admin.bar.siardsuite.util.I18n;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ValidationProperty {
    private final TextField field;
    private final Label validationMsgField;
    private final String validationMsg;
    private boolean isValid = true;

    public ValidationProperty(TextField field, Label validationMsgField, String validationMsg) {
        this.field = field;
        this.validationMsgField = validationMsgField;
        this.validationMsg = validationMsg;
    }

    public boolean validate() {
        if (this.field.getText().isEmpty()) {
            I18n.bind(this.validationMsgField.textProperty(), this.validationMsg);
            this.validationMsgField.setVisible(true);
            this.isValid = false;
        }
        return this.isValid;
    }
}
