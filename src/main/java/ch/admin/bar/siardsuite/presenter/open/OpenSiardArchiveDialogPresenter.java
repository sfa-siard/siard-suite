package ch.admin.bar.siardsuite.presenter.open;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import ch.admin.bar.siardsuite.view.DialogCloser;
import ch.admin.bar.siardsuite.view.ErrorHandler;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.ABSOLUTE_PATH;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.TIMESTAMP;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.NodePath.RECENT_FILES;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.sortedChildrenNames;

public class OpenSiardArchiveDialogPresenter {

    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton; // seems redundant
    @FXML
    protected HBox recentFilesHeader;
    @FXML
    protected Label recentFilesHeaderName;
    @FXML
    protected Label recentFilesHeaderDate;
    @FXML
    protected VBox recentFilesBox;
    @FXML
    protected Label dropFileTextTop;
    @FXML
    protected Label dropFileTextMiddle;
    @FXML
    protected VBox dropFileBox;
    @FXML
    protected MFXButton chooseFileButton;
    @FXML
    protected HBox buttonBox;

    private DialogCloser dialogCloser;
    private ErrorHandler errorHandler;
    private BiConsumer<File, Archive> onArchiveSelected;

    public void init(
            final DialogCloser dialogCloser,
            final ErrorHandler errorHandler,
            final BiConsumer<File, Archive> onArchiveSelected
    ) {
        this.dialogCloser = dialogCloser;
        this.errorHandler = errorHandler;
        this.onArchiveSelected = onArchiveSelected;

        I18n.bind(title.textProperty(), "open.siard.archive.dialog.title");
        I18n.bind(text.textProperty(), "open.siard.archive.dialog.text");

        I18n.bind(recentFilesHeaderName.textProperty(), "dialog.recent.files.header.name");
        I18n.bind(recentFilesHeaderDate.textProperty(), "dialog.recent.files.header.date");

        try {
            List<String> fileHashCodes = sortedChildrenNames(RECENT_FILES, TIMESTAMP, Comparator.reverseOrder());
            for (String fileHashCode : fileHashCodes) {
                try {
                    recentFilesBox.getChildren().add(getRecentFileBox(fileHashCode));
                } catch (IOException e) {
                    UserPreferences.remove(RECENT_FILES, fileHashCode);
                }
            }
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }

        if (recentFilesBox.getChildren().size() == 0) {
            showNoRecentFiles();
        } else {
            recentFilesBox.getChildren().removeIf(child -> recentFilesBox.getChildren().indexOf(child) > 2);
        }

        I18n.bind(dropFileTextTop.textProperty(), "dialog.drop.file.text.top");
        I18n.bind(dropFileTextMiddle.textProperty(), "dialog.drop.file.text.middle");

        dropFileBox.setOnDragOver(this::handleDragOver);
        dropFileBox.setOnDragDropped(this::handleDragDropped);

        I18n.bind(chooseFileButton.textProperty(), "open.siard.archive.dialog.choose.file.button");
        chooseFileButton.getStyleClass().setAll("button", "secondary");

        chooseFileButton.setOnAction(this::handleChooseFile);

        closeButton.setOnAction(event -> dialogCloser.closeDialog());

        buttonBox.getChildren().add(new CloseDialogButton(dialogCloser));
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }

    private void handleDragDropped(DragEvent event) {
        boolean completed = false;
        if (event.getDragboard().hasFiles()) {
            completed = true;
            if (event.getDragboard().getFiles().size() == 1) {
                final File file = event.getDragboard().getFiles().get(0);
                readArchive(file);
            }
        }
        event.setDropCompleted(completed);
        event.consume();
    }

    private void handleChooseFile(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n.get("open.siard.archive.dialog.choose.file.title"));
        final FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("SIARD files", "*.siard");
        fileChooser.getExtensionFilters().add(extensionFilter);
        final File file = fileChooser.showOpenDialog(title.getScene().getWindow());
        readArchive(file);
    }

    private void showNoRecentFiles() {
        recentFilesBox.setStyle("-fx-background-color: #f8f6f69e; -fx-alignment: center");
        final Label label = new Label(I18n.get("dialog.recent.files.nodata"));
        recentFilesBox.getChildren().add(label);
        label.setStyle("-fx-text-fill: #2a2a2a82");
    }

    private HBox getRecentFileBox(String fileHashCode) throws IOException {
        final HBox recentFileBox = new HBox();

        if (!fileHashCode.isEmpty()) {
            final Preferences preferences = UserPreferences.node(RECENT_FILES).node(fileHashCode);

            final String filePath = preferences.get(ABSOLUTE_PATH.name(), "");
            final File recentFile = new File(filePath);
            final BasicFileAttributes recentFileAttributes = Files.readAttributes(Paths.get(filePath),
                    BasicFileAttributes.class);

            final Label imageLabel = new Label();
            imageLabel.getStyleClass().add("icon-label");
            final Label nameLabel = new Label(recentFile.getName());
            nameLabel.getStyleClass().add("name-label");

            final String localeDate = I18n.getLocaleDate(recentFileAttributes.lastModifiedTime()
                    .toString()
                    .split("T")[0]);
            final Label dateLabel = new Label(localeDate);
            dateLabel.getStyleClass().add("date-label");

            recentFileBox.getChildren().addAll(imageLabel, nameLabel, dateLabel);
            recentFileBox.getStyleClass().add("file-hbox");
            VBox.setMargin(recentFileBox, new Insets(5, 0, 5, 0));

            recentFileBox.setOnMouseClicked(event -> readArchive(recentFile));
        }

        return recentFileBox;
    }

    private boolean isSiardArchive(File file) {
        final String filePath = file.getAbsolutePath();
        final int dotIndex = filePath.lastIndexOf(".");
        return dotIndex > -1 && filePath.substring(dotIndex + 1).equalsIgnoreCase("siard");
    }

    private void readArchive(File file) {
        if (file != null && isSiardArchive(file)) {
            try {
                final Archive archive = ArchiveImpl.newInstance();
                archive.open(file);
                dialogCloser.closeDialog();
                onArchiveSelected.accept(file, archive);
            } catch (IOException e) {
                errorHandler.handle(e);
            }
            try {
                final Preferences preferences = UserPreferences.push(RECENT_FILES,
                        TIMESTAMP,
                        Comparator.reverseOrder(),
                        String.valueOf(file.hashCode()));
                preferences.put(ABSOLUTE_PATH.name(), file.getAbsolutePath());
                preferences.put(TIMESTAMP.name(), String.valueOf(Clock.systemDefaultZone().millis()));
            } catch (BackingStoreException e) {
                errorHandler.handle(e);
            }
        }
    }

    public static LoadedFxml<OpenSiardArchiveDialogPresenter> load(
            final DialogCloser dialogCloser,
            final ErrorHandler errorHandler,
            final BiConsumer<File, Archive> onArchiveSelected
    ) {
        val loaded = FXMLLoadHelper.<OpenSiardArchiveDialogPresenter>load("fxml/open/open-siard-archive-dialog.fxml");
        loaded.getController().init(dialogCloser, errorHandler, onArchiveSelected);

        return loaded;
    }
}
