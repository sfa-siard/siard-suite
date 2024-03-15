package ch.admin.bar.siardsuite.presenter.option;

import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.component.DialogButton;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.preferences.Options;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import ch.admin.bar.siardsuite.view.DialogCloser;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.val;

public class OptionDialogPresenter {

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


    public void init(
            final UserPreferences userPreferences,
            final DialogCloser dialogCloser
            ) {

        I18n.bind(title.textProperty(), "option.dialog.title");
        I18n.bind(loginTimeoutLabel.textProperty(), "option.dialog.login-timeout.label");
        I18n.bind(loginTimeoutText.promptTextProperty(), "option.dialog.login-timeout.placeholder");
        I18n.bind(queryTimeoutLabel.textProperty(), "option.dialog.query-timeout.label");
        I18n.bind(queryTimeoutText.promptTextProperty(), "option.dialog.query-timeout.placeholder");

        closeButton.setOnAction(event -> dialogCloser.closeDialog());
        MFXButton save = new DialogButton(true, "button.save");
        buttonBox.getChildren().addAll(new CloseDialogButton(dialogCloser),
                save);
        save.setOnAction(event -> {
            userPreferences.push(Options.builder()
                    .queryTimeout(Integer.parseInt(queryTimeoutText.getText())) // FIXME input validation needed
                    .loginTimeout(Integer.parseInt(loginTimeoutText.getText())) // FIXME input validation needed
                    .build());
            dialogCloser.closeDialog();
        });

        val options = userPreferences.getStoredOptions();
        queryTimeoutText.setText(options.getQueryTimeout() + "");
        loginTimeoutText.setText(options.getLoginTimeout() + "");
    }

    public static LoadedFxml<OptionDialogPresenter> load(final ServicesFacade servicesFacade) {
        val loaded = FXMLLoadHelper.<OptionDialogPresenter>load("fxml/option/option-dialog.fxml");
        loaded.getController().init(servicesFacade.userPreferences(), servicesFacade.dialogs());

        return loaded;
    }
}
