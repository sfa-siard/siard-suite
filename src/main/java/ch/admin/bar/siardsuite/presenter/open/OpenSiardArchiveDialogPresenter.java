package ch.admin.bar.siardsuite.presenter.open;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.Preferences;
import ch.admin.bar.siardsuite.view.RootStage;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static ch.admin.bar.siardsuite.util.Preferences.PreferencesNode.RECENT_FILES;

public class OpenSiardArchiveDialogPresenter extends DialogPresenter {

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

    private final java.util.prefs.Preferences preferences = Preferences.get(RECENT_FILES);
    private final int max_recent_file_paths = 30;
    private final String[] recentFilePaths = new String[max_recent_file_paths];

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        title.textProperty().bind(I18n.createStringBinding("open.siard.archive.dialog.title"));
        text.textProperty().bind(I18n.createStringBinding("open.siard.archive.dialog.text"));

        recentFilesHeaderName.textProperty().bind(I18n.createStringBinding("open.siard.archive.dialog.recent.files.header.name"));
        recentFilesHeaderDate.textProperty().bind(I18n.createStringBinding("open.siard.archive.dialog.recent.files.header.date"));

        for (int i = 0; i < max_recent_file_paths; i++) {
            recentFilePaths[i] = preferences.get(String.valueOf(i), "");
        }

        for (String filePath : recentFilePaths) {
            if (!filePath.isEmpty()) {
                try {
                    recentFilesBox.getChildren().add(getRecentFileBox(filePath));
                } catch (IOException e) {
                    removeRecentFilePath(filePath);
                }
            }
        }

        if (recentFilesBox.getChildren().size() == 0) {
            showNoRecentFiles();
        } else {
            recentFilesBox.getChildren().removeIf(child -> recentFilesBox.getChildren().indexOf(child) > 2);
        }

        dropFileTextTop.textProperty().bind(I18n.createStringBinding("open.siard.archive.dialog.drop.file.text.top"));
        dropFileTextMiddle.textProperty().bind(I18n.createStringBinding("open.siard.archive.dialog.drop.file.text.middle"));

        dropFileBox.setOnDragOver(this::handleDragOver);
        dropFileBox.setOnDragDropped(this::handleDragDropped);

        chooseFileButton.textProperty().bind(I18n.createStringBinding("open.siard.archive.dialog.choose.file.button"));
        chooseFileButton.getStyleClass().setAll("button", "secondary");

        chooseFileButton.setOnAction(this::handleChooseFile);

        closeButton.setOnAction(event -> stage.closeDialog());

        buttonBox.getChildren().add(getCancelButton());
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
        final File file = fileChooser.showOpenDialog(stage);
        readArchive(file);
    }

    private void showNoRecentFiles() {
        recentFilesBox.setStyle("-fx-background-color: #f8f6f69e; -fx-alignment: center");
        final Label label = new Label(I18n.get("open.siard.archive.dialog.recent.files.nodata"));
        recentFilesBox.getChildren().add(label);
        label.setStyle("-fx-text-fill: #2a2a2a82");
    }

    private void addRecentFilePath(File file) {
        final int k = List.of(recentFilePaths).indexOf(file.getAbsolutePath());
        final int l = (k < 0) ? max_recent_file_paths - 1 : k;
        if (l > 0) {
            for (int j = 0; j < l; j++) {
                preferences.put(String.valueOf(j+1), recentFilePaths[j]);
            }
        }
        preferences.put("0", file.getAbsolutePath());
    }

    private void removeRecentFilePath(String filePath)  {
        final int l = List.of(recentFilePaths).indexOf(filePath);
        if (l >= 0) {
            preferences.remove(String.valueOf(l));
            for (int j = l + 1; j < max_recent_file_paths; j++) {
                preferences.put(String.valueOf(j-1), recentFilePaths[j]);
            }
        }
    }

    private HBox getRecentFileBox(String filePath) throws IOException {
        final HBox recentFileBox = new HBox();

        if (!filePath.isEmpty()) {
            final File recentFile = new File(filePath);
            final BasicFileAttributes recentFileAttributes = Files.readAttributes(Paths.get(filePath), BasicFileAttributes.class);

            final Label imageLabel = new Label();
            imageLabel.getStyleClass().add("icon-label");
            final Label nameLabel = new Label(recentFile.getName());
            nameLabel.getStyleClass().add("name-label");

            final String localeDate = I18n.getLocaleDate(recentFileAttributes.lastModifiedTime().toString().split("T")[0]);
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
                model.setArchive(file.getName(), archive);
                stage.closeDialog();
                stage.navigate(View.OPEN_SIARD_ARCHIVE_PREVIEW);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            addRecentFilePath(file);
        }
    }

}
