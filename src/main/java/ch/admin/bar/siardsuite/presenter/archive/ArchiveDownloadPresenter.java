package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.component.IconView;
import ch.admin.bar.siardsuite.component.LabelIcon;
import ch.admin.bar.siardsuite.component.Spinner;
import ch.admin.bar.siardsuite.component.SystemFileBrowser;
import ch.admin.bar.siardsuite.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.framework.general.DbInteractionService;
import ch.admin.bar.siardsuite.framework.general.Dialogs;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.archive.model.UserDefinedMetadata;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.ErrorHandler;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.CANCEL;
import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DOWNLOAD_FINISHED;
import static ch.admin.bar.siardsuite.component.ButtonBox.Type.TO_START;
import static ch.admin.bar.siardsuite.model.View.START;

public class ArchiveDownloadPresenter {

    @FXML
    public BorderPane borderPane;
    @FXML
    public Label title;
    @FXML
    public Label resultTitle;
    @FXML
    public ImageView loader;
    @FXML
    public Label recordsLoaded;
    @FXML
    public Label archivePath;
    @FXML
    public Label pathTitle;
    @FXML
    public Label openLink;
    @FXML
    public VBox fileSystemBox;
    @FXML
    public Label subtitle1;
    @FXML
    public ScrollPane resultBox;
    @FXML
    public VBox scrollBox;
    @FXML
    public MFXProgressBar progressBar;
    public VBox scrollBox2;
    @FXML
    private ButtonBox buttonsBox;

    private Spinner loadingSpinner;
    private long total;

    private ErrorHandler errorHandler;
    private UserDefinedMetadata userDefinedMetadata;
    private DbInteractionService dbInteractionService;
    private Dialogs dialogs;
    private Navigator navigator;

    public void init(
            final UserDefinedMetadata userDefinedMetadata,
            final StepperNavigator<Void> navigator,
            final ServicesFacade servicesFacade
    ) {
        this.errorHandler = servicesFacade.errorHandler();
        this.userDefinedMetadata = userDefinedMetadata;
        this.dbInteractionService = servicesFacade.dbInteractionService();
        this.dialogs = servicesFacade.dialogs();
        this.navigator = servicesFacade.navigator();

        this.loader.setImage(Icon.loading);
        loadingSpinner = new Spinner(this.loader);
        this.bindTexts();
        this.buttonsBox = new ButtonBox().make(CANCEL);
        addButtons();
        setListeners();

        this.openLink.setOnMouseClicked(event -> openArchiveDirectory(userDefinedMetadata.getSaveAt()));
        this.archivePath.setText(userDefinedMetadata.getSaveAt().getAbsolutePath());
        this.subtitle1.setText(userDefinedMetadata.getDbName());

        downloadAndArchiveDatabase();
    }

    private void setListeners() {
        if (this.buttonsBox.cancel() != null) {
            if (this.resultTitle.isVisible()) {
                this.buttonsBox.cancel().setOnAction((event) -> {
                    try {
                        final Archive archive = ArchiveImpl.newInstance();
                        archive.open(userDefinedMetadata.getSaveAt());
                        navigator.openArchive(archive);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                this.buttonsBox.cancel().setOnAction((event) -> dialogs.openDialog(View.ARCHIVE_ABORT_DIALOG));
            }
        }
        if (this.buttonsBox.next() != null) {
            this.buttonsBox.next().setOnAction((event) -> navigator.navigate(START));
        }
    }

    private void bindTexts() {
        I18n.bind(title.textProperty(), "archiveDownload.view.title.inProgress");
        I18n.bind(recordsLoaded.textProperty(), "archiveDownload.view.message.inProgress");
        I18n.bind(pathTitle.textProperty(), "archiveDownload.view.pathTitle");
        I18n.bind(openLink.textProperty(), "archiveDownload.view.openLink");
    }

    private void downloadAndArchiveDatabase() {
        loadingSpinner.play();

        try {
            dbInteractionService.execute(LoadDatabaseInstruction.builder()
                    .connectionData(userDefinedMetadata.getDbmsConnectionData())
                    .saveAt(userDefinedMetadata.getSaveAt())
                    .loadOnlyMetadata(false)
                    .viewsAsTables(userDefinedMetadata.getExportViewsAsTables())
                    .onSuccess(event -> handleDownloadSuccess())
                    .onFailure(event -> handleDownloadFailure(event.getSource().getException()))
                    .onSingleValueCompleted((observable, oldValue, newValue) -> {
                        AtomicInteger pos = new AtomicInteger();
                        newValue.forEach(p ->
                                addLoadingData(p.getKey(), p.getValue(), pos.getAndIncrement())
                        );
                    })
                    .onProgress((observable, oldValue, newValue) -> {
                        double pos = newValue.doubleValue();
                        progressBar.progressProperty().set(pos);
                    })
                    .build());
        } catch (SQLException e) {
            errorHandler.handle(e);
            ;
        }
    }

    private void handleDownloadSuccess() {
        loadingSpinner.hide();
        this.title.setVisible(false);
        this.resultTitle.setVisible(true);
        this.fileSystemBox.setVisible(true);
        I18n.bind(resultTitle.textProperty(), "archiveDownload.view.title.success");
        resultTitle.getStyleClass().setAll("ok-circle-icon", "h2", "label-icon-left");
        setResultData();

        this.buttonsBox = new ButtonBox().make(DOWNLOAD_FINISHED);
        addButtons();
    }

    private void handleDownloadFailure(final Throwable throwable) {
        this.title.setVisible(false);
        loadingSpinner.hide();
        progressBar.setVisible(false);
        this.resultTitle.setVisible(true);
        this.subtitle1.setVisible(false);
        this.resultBox.setVisible(false);
        I18n.bind(resultTitle.textProperty(), "archiveDownload.view.title.failed");
        I18n.bind(recordsLoaded.textProperty(), "archiveDownload.view.message.failed");
        resultTitle.getStyleClass().setAll("x-circle-icon", "h2", "label-icon-left");
        this.buttonsBox = new ButtonBox().make(TO_START);
        errorHandler.handle(throwable);
        addButtons();
    }

    private void addButtons() {
        this.borderPane.setBottom(buttonsBox);
        setListeners();
    }

    private void openArchiveDirectory(File file) { // TODO move to services
        try {
            new SystemFileBrowser(file).show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setResultData() {
        // remove progressbar
        progressBar.setVisible(false);
        scrollBox.setVisible(false);
        scrollBox2.setVisible(true);
        I18n.bind(recordsLoaded.textProperty(), "archiveDownload.view.message.success", total);
    }


    private void addLoadingData(String text, Long rows, Integer pos) {
        if (rows < 0) {
            // set previous to ok
            if (scrollBox.getChildren().size() > 0) {
                int itemPos = scrollBox.getChildren().size() - 1;
                LabelIcon label = (LabelIcon) scrollBox.getChildren().get(itemPos);
                label.setGraphic(new IconView(itemPos, IconView.IconType.OK));
            }
            scrollBox.getChildren().add(
                    new LabelIcon(text, pos, IconView.IconType.LOADING));
        } else {
            LabelIcon label = new LabelIcon(text, pos, IconView.IconType.OK);
            I18n.bind(label.textProperty(), "upload.result.success.table.rows", text, rows);
            scrollBox2.getChildren().add(label);
            total += rows;
        }
    }

    public static LoadedFxml<ArchiveDownloadPresenter> load(
            final UserDefinedMetadata userDefinedMetadata,
            final StepperNavigator<Void> navigator,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<ArchiveDownloadPresenter>load("fxml/archive/archive-download.fxml");
        loaded.getController().init(userDefinedMetadata, navigator, servicesFacade);

        return loaded;
    }
}
