package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.prefs.Preferences;

public class OpenSiardFileDialogPresenter extends DialogPresenter {

    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton; // seems redundant
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

    private void readArchive(File file) {
        if (file != null) {
            addRecentFile(file);
        }
    }

    private void addRecentFile(File file) {
        final int max_recent_files = 30;
        final String[] recentFiles = new String[max_recent_files];
        for (int i = 0; i < max_recent_files; i++) {
            recentFiles[i] = preferences.get(String.valueOf(i), "");
        }

        final int k = List.of(recentFiles).indexOf(file.getAbsolutePath());
        final int l = (k < 0) ? max_recent_files - 1 : k;

        if (l > 0) {
            for (int j = 0; j < l; j++) {
                preferences.put(String.valueOf(j+1), recentFiles[j]);
            }
        }
        preferences.put("0", file.getAbsolutePath());
    }

    private String[] getRecentFiles() {
        final int num_recent_files = 3;
        final String[] recentFiles = new String[num_recent_files];
        for (int i = 0; i < num_recent_files; i++) {
            recentFiles[i] = preferences.get(String.valueOf(i), "");
        }
        return recentFiles;
    }

}
