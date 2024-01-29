package ch.admin.bar.siardsuite.presenter.option;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.component.DialogButton;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.preferences.Options;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.val;

public class OptionDialogPresenter extends DialogPresenter {

    @FXML
    protected Label title;
    @FXML
    protected MFXButton closeButton;
    @FXML
    protected HBox buttonBox;
    @FXML
    public TextField queryTimeoutText;
    @FXML
    public TextField  loginTimeoutText;
    @FXML
    public Label loginTimeoutLabel;
    @FXML
    public Label queryTimeoutLabel;


    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

        I18n.bind(title.textProperty(), "option.dialog.title");
        I18n.bind(loginTimeoutLabel.textProperty(), "option.dialog.login-timeout.label");
        I18n.bind(loginTimeoutText.promptTextProperty(), "option.dialog.login-timeout.placeholder");
        I18n.bind(queryTimeoutLabel.textProperty(), "option.dialog.query-timeout.label");
        I18n.bind(queryTimeoutText.promptTextProperty(), "option.dialog.query-timeout.placeholder");

        closeButton.setOnAction(event -> stage.closeDialog());
        MFXButton save = new DialogButton(true, "button.save");
        buttonBox.getChildren().addAll(new CloseDialogButton(this.stage),
                save);
        save.setOnAction(event -> saveOptions());

        initFormFields();
    }

    private void saveOptions() {
        UserPreferences.INSTANCE.push(Options.builder()
                .queryTimeout(Integer.parseInt(queryTimeoutText.getText())) // FIXME input validation needed
                .loginTimeout(Integer.parseInt(loginTimeoutText.getText())) // FIXME input validation needed
                .build());
        stage.closeDialog();
    }

    private void initFormFields() {
        val options = UserPreferences.INSTANCE.getStoredOptions();
        queryTimeoutText.setText(options.getQueryTimeout() + "");
        loginTimeoutText.setText(options.getLoginTimeout() + "");
    }
}
