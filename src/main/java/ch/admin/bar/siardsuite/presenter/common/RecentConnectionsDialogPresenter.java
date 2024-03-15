package ch.admin.bar.siardsuite.presenter.common;

import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.framework.DialogCloser;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.service.preferences.RecentDbConnection;
import ch.admin.bar.siardsuite.service.preferences.StorageData;
import ch.admin.bar.siardsuite.service.preferences.UserPreferences;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.val;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RecentConnectionsDialogPresenter {

    private static final I18nKey ARCHIVE_TITLE = I18nKey.of("archiveDbDialog.title");
    private static final I18nKey ARCHIVE_TEXT = I18nKey.of("archiveDbDialog.text");

    private static final I18nKey UPLOAD_TITLE = I18nKey.of("uploadDbDialog.title");
    private static final I18nKey UPLOAD_TEXT = I18nKey.of("uploadDbDialog.text");

    private static final I18nKey NEW_CONNECTION = I18nKey.of("archiveDbDialog.btnNewConnection");
    private static final I18nKey RECENT_CONNECTIONS_HEADER_NAME = I18nKey.of("dialog.recent.connections.header.name");
    private static final I18nKey RECENT_CONNECTIONS_HEADER_DATE = I18nKey.of("dialog.recent.connections.header.date");
    private static final I18nKey NO_DATA = I18nKey.of("dialog.recent.connections.nodata");

    @FXML
    protected Label title;
    @FXML
    public Text text;
    @FXML
    protected MFXButton closeButton;
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

    public void init(
            final DialogCloser dialogCloser,
            final Runnable onNewConnection,
            final Consumer<RecentDbConnection> onRecentConnectionSelected,
            final DisplayableText titleText,
            final DisplayableText descriptionText
    ) {
        title.textProperty().bind(titleText.bindable());
        text.textProperty().bind(descriptionText.bindable());

        newConnectionButton.textProperty().bind(DisplayableText.of(NEW_CONNECTION).bindable());
        recentConnectionsHeaderName.textProperty().bind(DisplayableText.of(RECENT_CONNECTIONS_HEADER_NAME).bindable());
        recentConnectionsHeaderDate.textProperty().bind(DisplayableText.of(RECENT_CONNECTIONS_HEADER_DATE).bindable());

        newConnectionButton.setOnAction(event -> {
            dialogCloser.closeDialog();
            onNewConnection.run();
        });
        closeButton.setOnAction(event -> dialogCloser.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(dialogCloser));

        val boxes = UserPreferences.INSTANCE.getStoredConnections().stream()
                .map(dbConnectionStorageData -> createConnectionsBox(
                        dbConnectionStorageData,
                        () -> {
                            dialogCloser.closeDialog();
                            onRecentConnectionSelected.accept(dbConnectionStorageData.getStoredData());
                        }
                ))
                .limit(3)
                .collect(Collectors.toList());

        if (boxes.isEmpty()) {
            showNoRecentConnections();
        } else {
            recentConnectionsBox.getChildren().addAll(boxes);
        }
    }

    private void showNoRecentConnections() {
        recentConnectionsBox.getChildren().clear();
        recentConnectionsBox.setStyle("-fx-background-color: #f8f6f69e; -fx-alignment: center");
        final Label label = new Label();
        label.textProperty().bind(DisplayableText.of(NO_DATA).bindable());
        recentConnectionsBox.getChildren().add(label);
        label.setStyle("-fx-text-fill: #2a2a2a82");
    }

    private HBox createConnectionsBox(
            final StorageData<RecentDbConnection> storedConnection,
            final Runnable onAction
    ) {
        val imageLabel = new Label();
        imageLabel.getStyleClass().add("link-icon");

        val nameLabel = new Label(storedConnection.getStoredData().getName());
        nameLabel.getStyleClass().add("name-label");

        val dateLabel = new Label(storedConnection.getStoredAtDate());
        dateLabel.getStyleClass().add("date-label");

        val recentConnectionsBox = new HBox(imageLabel, nameLabel, dateLabel);
        recentConnectionsBox.getStyleClass().add("connections-hbox");
        recentConnectionsBox.setOnMouseClicked(event -> onAction.run());

        VBox.setMargin(recentConnectionsBox, new Insets(5, 0, 5, 0));

        return recentConnectionsBox;
    }

    public static LoadedView<RecentConnectionsDialogPresenter> loadForUpload(
            final Consumer<Optional<RecentDbConnection>> onRecentConnectionSelected,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<RecentConnectionsDialogPresenter>load("fxml/archive/archive-db-dialog.fxml");

        loaded.getController().init(
                servicesFacade.dialogs(),
                () -> onRecentConnectionSelected.accept(Optional.empty()),
                recentDbConnection -> onRecentConnectionSelected.accept(Optional.of(recentDbConnection)),
                DisplayableText.of(UPLOAD_TITLE),
                DisplayableText.of(UPLOAD_TEXT));

        return loaded;
    }

    public static LoadedView<RecentConnectionsDialogPresenter> loadForArchiving(
            final Consumer<Optional<RecentDbConnection>> onRecentConnectionSelected,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<RecentConnectionsDialogPresenter>load("fxml/archive/archive-db-dialog.fxml");

        loaded.getController().init(
                servicesFacade.dialogs(),
                () -> onRecentConnectionSelected.accept(Optional.empty()),
                recentDbConnection -> onRecentConnectionSelected.accept(Optional.of(recentDbConnection)),
                DisplayableText.of(ARCHIVE_TITLE),
                DisplayableText.of(ARCHIVE_TEXT));

        return loaded;
    }
}
