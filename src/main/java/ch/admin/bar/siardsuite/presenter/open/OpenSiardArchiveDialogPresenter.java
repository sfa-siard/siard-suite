package ch.admin.bar.siardsuite.presenter.open;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.framework.DialogCloser;
import ch.admin.bar.siardsuite.framework.ErrorHandler;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.service.preferences.RecentFile;
import ch.admin.bar.siardsuite.service.preferences.StorageData;
import ch.admin.bar.siardsuite.service.preferences.UserPreferences;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
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
import java.util.function.BiConsumer;

public class OpenSiardArchiveDialogPresenter {

    private static final I18nKey TITLE = I18nKey.of("open.siard.archive.dialog.title");
    private static final I18nKey TEXT = I18nKey.of("open.siard.archive.dialog.text");
    private static final I18nKey HEADER_NAME = I18nKey.of("dialog.recent.files.header.name");
    private static final I18nKey HEADER_DATE = I18nKey.of("dialog.recent.files.header.date");
    private static final I18nKey DROP_FILE_TEXT_TOP = I18nKey.of("dialog.drop.file.text.top");
    private static final I18nKey DROP_FILE_TEXT_MIDDLE = I18nKey.of("dialog.drop.file.text.middle");
    private static final I18nKey CHOOSE_FILE_BUTTON = I18nKey.of("open.siard.archive.dialog.choose.file.button");
    private static final I18nKey FILE_CHOOSER_TITLE = I18nKey.of("open.siard.archive.dialog.choose.file.title");
    private static final I18nKey NO_DATA = I18nKey.of("dialog.recent.files.nodata");


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

        title.textProperty().bind(DisplayableText.of(TITLE).bindable());
        text.textProperty().bind(DisplayableText.of(TEXT).bindable());
        recentFilesHeaderName.textProperty().bind(DisplayableText.of(HEADER_NAME).bindable());
        recentFilesHeaderDate.textProperty().bind(DisplayableText.of(HEADER_DATE).bindable());
        dropFileTextTop.textProperty().bind(DisplayableText.of(DROP_FILE_TEXT_TOP).bindable());
        dropFileTextMiddle.textProperty().bind(DisplayableText.of(DROP_FILE_TEXT_MIDDLE).bindable());
        chooseFileButton.textProperty().bind(DisplayableText.of(CHOOSE_FILE_BUTTON).bindable());

        UserPreferences.INSTANCE.getRecentFiles()
                .forEach(recentFileStorageData -> recentFilesBox.getChildren()
                        .add(getRecentFileBox(recentFileStorageData)));

        if (recentFilesBox.getChildren().size() == 0) {
            showNoRecentFiles();
        } else {
            recentFilesBox.getChildren().removeIf(child -> recentFilesBox.getChildren().indexOf(child) > 2);
        }

        dropFileBox.setOnDragOver(this::handleDragOver);
        dropFileBox.setOnDragDropped(this::handleDragDropped);

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
        fileChooser.setTitle(DisplayableText.of(FILE_CHOOSER_TITLE).getText());
        final FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("SIARD files", "*.siard");
        fileChooser.getExtensionFilters().add(extensionFilter);
        final File file = fileChooser.showOpenDialog(title.getScene().getWindow());
        readArchive(file);
    }

    private void showNoRecentFiles() {
        recentFilesBox.setStyle("-fx-background-color: #f8f6f69e; -fx-alignment: center");
        final Label label = new Label(DisplayableText.of(NO_DATA).getText());
        recentFilesBox.getChildren().add(label);
        label.setStyle("-fx-text-fill: #2a2a2a82");
    }

    private HBox getRecentFileBox(final StorageData<RecentFile> recentFileStorageData) {
        final HBox recentFileBox = new HBox();

        final Label imageLabel = new Label();
        imageLabel.getStyleClass().add("icon-label");
        final Label nameLabel = new Label(recentFileStorageData.getStoredData().getValue().getName());
        nameLabel.getStyleClass().add("name-label");

        final Label dateLabel = new Label(recentFileStorageData.getStoredAtDate());
        dateLabel.getStyleClass().add("date-label");

        recentFileBox.getChildren().addAll(imageLabel, nameLabel, dateLabel);
        recentFileBox.getStyleClass().add("file-hbox");
        VBox.setMargin(recentFileBox, new Insets(5, 0, 5, 0));

        recentFileBox.setOnMouseClicked(event -> readArchive(recentFileStorageData.getStoredData().getValue()));

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
            UserPreferences.INSTANCE.push(new RecentFile(file));
        }
    }

    public static LoadedView<OpenSiardArchiveDialogPresenter> load(
            final BiConsumer<File, Archive> onArchiveSelected,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<OpenSiardArchiveDialogPresenter>load("fxml/open/open-siard-archive-dialog.fxml");
        loaded.getController().init(
                servicesFacade.dialogs(),
                servicesFacade.errorHandler(),
                onArchiveSelected);

        return loaded;
    }
}
