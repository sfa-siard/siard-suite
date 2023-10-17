package ch.admin.bar.siardsuite.presenter.search;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.Optional;
import java.util.function.Consumer;

public class SearchTableDialogPresenter extends DialogPresenter {

    @FXML
    private Label title;
    @FXML
    private Text text;
    @FXML
    private MFXButton closeButton;

    private MFXButton searchButton;
    @FXML
    private TextField searchField;
    @FXML
    private HBox buttonBox;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

        I18n.bind(title.textProperty(), "search.table.dialog.title");
        I18n.bind(text.textProperty(), "search.table.dialog.text");

        closeButton.setOnAction(event -> stage.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(this.stage));

        searchButton = new MFXButton();
        I18n.bind(searchButton.textProperty(), "search.table.dialog.search");
        searchButton.getStyleClass().add("primary");
        buttonBox.getChildren().add(searchButton);
    }

    public void registerResultListener(Consumer<Optional<String>> resultConsumer) {
        closeButton.setOnAction(event -> {
            stage.closeDialog();
            resultConsumer.accept(Optional.empty());
        });

        searchButton.setOnAction(event -> {
            stage.closeDialog();
            resultConsumer.accept(Optional.ofNullable(searchField.getText()));
        });
    }
}
