package ch.admin.bar.siardsuite.ui.presenter.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.errors.ErrorHandler;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.model.Tuple;
import ch.admin.bar.siardsuite.model.UserDefinedMetadata;
import ch.admin.bar.siardsuite.service.ArchiveHandler;
import ch.admin.bar.siardsuite.service.DbInteractionService;
import ch.admin.bar.siardsuite.service.FilesService;
import ch.admin.bar.siardsuite.service.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.service.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.ui.View;
import ch.admin.bar.siardsuite.ui.common.Icon;
import ch.admin.bar.siardsuite.ui.component.ButtonBox;
import ch.admin.bar.siardsuite.ui.component.IconView;
import ch.admin.bar.siardsuite.ui.component.LabelIcon;
import ch.admin.bar.siardsuite.ui.component.Spinner;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.ui.View.START;
import static ch.admin.bar.siardsuite.ui.component.ButtonBox.Type.CANCEL;
import static ch.admin.bar.siardsuite.ui.component.ButtonBox.Type.DOWNLOAD_FINISHED;
import static ch.admin.bar.siardsuite.ui.component.ButtonBox.Type.TO_START;

@Slf4j
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

    private UserDefinedMetadata userDefinedMetadata;
    private DbmsConnectionData connectionData;

    private ErrorHandler errorHandler;
    private DbInteractionService dbInteractionService;
    private Dialogs dialogs;
    private Navigator navigator;
    private ArchiveHandler archiveHandler;
    private FilesService filesService;

    public void init(
            final UserDefinedMetadata userDefinedMetadata,
            final DbmsConnectionData connectionData,
            final ErrorHandler errorHandler,
            final DbInteractionService dbInteractionService,
            final Dialogs dialogs,
            final Navigator navigator,
            final ArchiveHandler archiveHandler,
            final FilesService filesService
    ) {
        this.userDefinedMetadata = userDefinedMetadata;
        this.connectionData = connectionData;

        this.errorHandler = errorHandler;
        this.dbInteractionService = dbInteractionService;
        this.dialogs = dialogs;
        this.navigator = navigator;
        this.archiveHandler = archiveHandler;
        this.filesService = filesService;

        this.loader.setImage(Icon.LOADING.toImage());
        loadingSpinner = new Spinner(this.loader);
        this.bindTexts();
        this.buttonsBox = new ButtonBox().make(CANCEL);
        addButtons();
        setListeners();

        this.openLink.setOnMouseClicked(event -> filesService.openInFileBrowser(userDefinedMetadata.getSaveAt()));
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
                        navigator.navigate(View.OPEN_SIARD_ARCHIVE_PREVIEW, archive);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                this.buttonsBox.cancel().setOnAction((event) -> dialogs.open(View.ARCHIVE_ABORT_DIALOG));
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

        dbInteractionService.execute(LoadDatabaseInstruction.builder()
                .connectionData(connectionData)
                .saveAt(userDefinedMetadata.getSaveAt())
                .loadOnlyMetadata(false)
                .viewsAsTables(userDefinedMetadata.getExportViewsAsTables())
                .onSuccess(this::handleDownloadSuccess)
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
    }

    private void handleDownloadSuccess(Archive archive) {
        userDefinedMetadata.writeTo(archive.getMetaData());
        archiveHandler.save(archive, userDefinedMetadata.getSaveAt());

        log.info("Downloaded archive successfully stored to {}", userDefinedMetadata.getSaveAt().getAbsolutePath());

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

    public static LoadedView<ArchiveDownloadPresenter> load(
            final Tuple<UserDefinedMetadata, DbmsConnectionData> data,
            final StepperNavigator<Void> navigator,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<ArchiveDownloadPresenter>load("fxml/archive/archive-download.fxml");
        loaded.getController().init(
                data.getValue1(),
                data.getValue2(),
                servicesFacade.errorHandler(),
                servicesFacade.getService(DbInteractionService.class),
                servicesFacade.dialogs(),
                servicesFacade.navigator(),
                servicesFacade.getService(ArchiveHandler.class),
                servicesFacade.getService(FilesService.class)
        );

        return loaded;
    }
}
