package ch.admin.bar.siardsuite.presenter.archive.browser.dialogues;

import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.DialogCloser;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.val;

import java.util.Optional;
import java.util.function.Consumer;

public class SearchTableDialogPresenter {

    private static final I18nKey TITLE = I18nKey.of("search.table.dialog.title");
    private static final I18nKey TEXT = I18nKey.of("search.table.dialog.text");
    private static final I18nKey SEARCH = I18nKey.of("search.table.dialog.search");

    @FXML
    private Label title;
    @FXML
    private Text text;
    @FXML
    private MFXButton closeButton;
    @FXML
    private TextField searchField;
    @FXML
    private HBox buttonBox;

    public void init(DialogCloser dialogCloser, Consumer<Optional<String>> resultConsumer) {
        closeButton.setOnAction(event -> {
            dialogCloser.closeDialog();
            resultConsumer.accept(Optional.empty());
        });

        val closeButton = new CloseDialogButton(dialogCloser);
        closeButton.setOnAction(event -> {
            dialogCloser.closeDialog();
            resultConsumer.accept(Optional.empty());
        });

        val searchButton = new MFXButton();
        searchButton.getStyleClass().add("primary");
        searchButton.textProperty().bind(DisplayableText.of(SEARCH).bindable());
        searchButton.setOnAction(event -> {
            dialogCloser.closeDialog();
            resultConsumer.accept(Optional.ofNullable(searchField.getText()));
        });

        buttonBox.getChildren().setAll(closeButton, searchButton);

        title.textProperty().bind(DisplayableText.of(TITLE).bindable());
        text.textProperty().bind(DisplayableText.of(TEXT).bindable());
    }

    public static LoadedFxml<SearchTableDialogPresenter> load(
            final DialogCloser dialogCloser,
            final Consumer<Optional<String>> resultConsumer
    ) {
        val loaded = FXMLLoadHelper.<SearchTableDialogPresenter>load("fxml/search/search-table-dialog.fxml");
        loaded.getController().init(dialogCloser, resultConsumer);

        return loaded;
    }
}
