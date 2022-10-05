package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;

import io.github.palexdev.materialfx.controls.MFXButton;

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
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class OpenSiardFileDialogPresenter extends DialogPresenter {

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

    private final Preferences preferences = Preferences.userRoot().node(this.getClass().getName());

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        setTitle("open.siard.file.dialog.title");
        text.textProperty().bind(I18n.createStringBinding("open.siard.file.dialog.text"));

        if (Arrays.stream(getRecentFilePaths()).anyMatch(path -> !path.isEmpty())) {
            recentFilesHeaderName.textProperty().bind(I18n.createStringBinding("open.siard.file.recent.files.header.name"));
            recentFilesHeaderDate.textProperty().bind(I18n.createStringBinding("open.siard.file.recent.files.header.date"));
            recentFilesHeader.setVisible(true);

            for (String filePath : getRecentFilePaths()) {
                try {
                    recentFilesBox.getChildren().add(getRecentFileBox(filePath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            recentFilesBox.setVisible(true);
        }

        dropFileTextTop.textProperty().bind(I18n.createStringBinding("open.siard.file.drop.file.text.top"));
        dropFileTextMiddle.textProperty().bind(I18n.createStringBinding("open.siard.file.drop.file.text.middle"));

        dropFileBox.setOnDragOver(this::handleDragOver);
        dropFileBox.setOnDragDropped(this::handleDragDropped);

        chooseFileButton.textProperty().bind(I18n.createStringBinding("open.siard.file.choose.file.button"));
        chooseFileButton.getStyleClass().setAll("button", "secondary");

        chooseFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(I18n.get("open.siard.file.choose.file.title"));
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("SIARD files", "*.siard");
            fileChooser.getExtensionFilters().add(extensionFilter);
            readArchive(fileChooser.showOpenDialog(stage));
        });

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
                final String filePath = file.getAbsolutePath();
                final int dotIndex = filePath.lastIndexOf(".");
                if (dotIndex > -1 && filePath.substring(dotIndex + 1).equalsIgnoreCase("siard")) {
                    readArchive(file);
                }
            }
        }
        event.setDropCompleted(completed);
        event.consume();
    }

    private void readArchive(File file) {
        if (file != null) {
            addRecentFilePath(file);
        }
    }

    private void addRecentFilePath(File file) {
        final int max_recent_file_paths = 30;
        final String[] recentFilePaths = new String[max_recent_file_paths];
        for (int i = 0; i < max_recent_file_paths; i++) {
            recentFilePaths[i] = preferences.get(String.valueOf(i), "");
        }

        final int k = List.of(recentFilePaths).indexOf(file.getAbsolutePath());
        final int l = (k < 0) ? max_recent_file_paths - 1 : k;

        if (l > 0) {
            for (int j = 0; j < l; j++) {
                preferences.put(String.valueOf(j+1), recentFilePaths[j]);
            }
        }
        preferences.put("0", file.getAbsolutePath());
    }

    private String[] getRecentFilePaths() {
        final int num_recent_file_paths = 3;
        final String[] recentFilePaths = new String[num_recent_file_paths];
        for (int i = 0; i < num_recent_file_paths; i++) {
            recentFilePaths[i] = preferences.get(String.valueOf(i), "");
        }
        return recentFilePaths;
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

}
