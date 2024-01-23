package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.STORAGE_DATE;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.TIMESTAMP;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.NodePath.DATABASE_CONNECTION;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.sortedChildrenNames;

public class UploadDbDialogPresenter extends DialogPresenter {

    @FXML
    protected Label title;
    @FXML
    public Text text;
    @FXML
    protected MFXButton closeButton; // seems redundant
    @FXML
    public MFXButton newConnectionButton;
    @FXML
    public HBox recentConnectionsHeader;
    @FXML
    public Label recentConnectionsHeaderName;
    @FXML
    public Label recentConnectionsHeaderDate;
    @FXML
    public VBox recentConnectionsBox;
    @FXML
    protected HBox buttonBox;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

        I18n.bind(title.textProperty(), "uploadDbDialog.title");
        I18n.bind(text.textProperty(), "uploadDbDialog.text");
        I18n.bind(newConnectionButton.textProperty(),"archiveDbDialog.btnNewConnection");

        newConnectionButton.setOnAction(event -> {
            stage.closeDialog();
            stage.navigate(View.UPLOAD_STEPPER);
        });

        I18n.bind(recentConnectionsHeaderName.textProperty(),"dialog.recent.connections.header.name");
        I18n.bind(recentConnectionsHeaderDate.textProperty(), "dialog.recent.connections.header.date");

        try {
            List<String> connectionNames = sortedChildrenNames(DATABASE_CONNECTION, TIMESTAMP, Comparator.reverseOrder());
            for (String connectionName : connectionNames) {
                recentConnectionsBox.getChildren().add(getRecentConnectionsBox(connectionName));
            }
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }

        if (recentConnectionsBox.getChildren().size() == 0) {
            showNoRecentConnections();
        } else {
            recentConnectionsBox.getChildren().removeIf(child -> recentConnectionsBox.getChildren().indexOf(child) > 2);
        }

        closeButton.setOnAction(event -> stage.closeDialog());

        buttonBox.getChildren().add(new CloseDialogButton(this.stage));
    }

    private void showNoRecentConnections() {
        recentConnectionsBox.setStyle("-fx-background-color: #f8f6f69e; -fx-alignment: center");
        final Label label = new Label(I18n.get("dialog.recent.connections.nodata"));
        recentConnectionsBox.getChildren().add(label);
        label.setStyle("-fx-text-fill: #2a2a2a82");
    }

    private HBox getRecentConnectionsBox(String connectionName) {
        final HBox recentConnectionsBox = new HBox();

        if (!connectionName.isEmpty()) {
            final Preferences preferences = UserPreferences.node(DATABASE_CONNECTION).node(connectionName);

            final Label imageLabel = new Label();
            imageLabel.getStyleClass().add("link-icon");
            final Label nameLabel = new Label(connectionName);
            nameLabel.getStyleClass().add("name-label");

            final String localeDate = preferences.get(STORAGE_DATE.name(), "");
            final Label dateLabel = new Label(localeDate);
            dateLabel.getStyleClass().add("date-label");

            recentConnectionsBox.getChildren().addAll(imageLabel, nameLabel, dateLabel);
            recentConnectionsBox.getStyleClass().add("connections-hbox");
            VBox.setMargin(recentConnectionsBox, new Insets(5, 0, 5, 0));

            recentConnectionsBox.setOnMouseClicked(event -> showRecentConnection(connectionName));
        }

        return recentConnectionsBox;
    }

    private void showRecentConnection(String connectionName) {
//        controller.recentDatabaseConnection = connectionName; TODO FIXME
        stage.closeDialog();
        stage.navigate(View.UPLOAD_STEPPER);
    }

}
