package ch.admin.bar.siardsuite.presenter.option;

import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.component.DialogButton;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.UserPreferences;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.prefs.Preferences;

import static ch.admin.bar.siardsuite.util.UserPreferences.KeyIndex.LOGIN_TIMEOUT;
import static ch.admin.bar.siardsuite.util.UserPreferences.KeyIndex.QUERY_TIMEOUT;
import static ch.admin.bar.siardsuite.util.UserPreferences.NodePath.OPTIONS;

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
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
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
        final Preferences preferences = UserPreferences.node(OPTIONS);
        preferences.put(QUERY_TIMEOUT.name(), queryTimeoutText.getText());
        preferences.put(LOGIN_TIMEOUT.name(), loginTimeoutText.getText());
        stage.closeDialog();
    }

    private void initFormFields() {
        final Preferences preferences = UserPreferences.node(OPTIONS);
        queryTimeoutText.setText(preferences.get(QUERY_TIMEOUT.name(), "0"));
        loginTimeoutText.setText(preferences.get(LOGIN_TIMEOUT.name(), "0"));
    }

}
