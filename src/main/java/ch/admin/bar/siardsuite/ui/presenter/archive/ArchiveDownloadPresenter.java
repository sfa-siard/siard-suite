package ch.admin.bar.siardsuite.ui.presenter.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.errors.ErrorHandler;
import ch.admin.bar.siardsuite.framework.hooks.Destructible;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKeyArg;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKeyArgArg;
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
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class ArchiveDownloadPresenter implements Destructible {


    private static final I18nKey BUTTON_HOME = I18nKey.of("button.home");
    private static final I18nKey BUTTON_CANCEL = I18nKey.of("button.cancel");
    private static final I18nKey BUTTON_VIEW_ARCHIVE = I18nKey.of("button.view-archive");

    private static final I18nKey IN_PROGRESS_TITLE = I18nKey.of("archiveDownload.view.title.inProgress");
    private static final I18nKey IN_PROGRESS_MESSAGE = I18nKey.of("archiveDownload.view.message.inProgress");
    private static final I18nKey PATH_TITLE = I18nKey.of("archiveDownload.view.pathTitle");
    private static final I18nKey OPEN_LINK = I18nKey.of("archiveDownload.view.openLink");
    private static final I18nKey SUCCESS_TITLE = I18nKey.of("archiveDownload.view.title.success");
    private static final I18nKeyArg<Long> SUCCESS_MESSAGE = I18nKeyArg.of("archiveDownload.view.message.success");
    private static final I18nKey FAILED_TITLE = I18nKey.of("archiveDownload.view.title.failed");
    private static final I18nKey FAILED_MESSAGE = I18nKey.of("archiveDownload.view.message.failed");

    private static final I18nKeyArgArg<String, Long> TABLE_ROWS = I18nKeyArgArg.of("upload.result.success.table.rows");

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

    private Spinner loadingSpinner;
    private long total;

    private UserDefinedMetadata userDefinedMetadata;
    private DbmsConnectionData connectionData;

    private ErrorHandler errorHandler;
    private DbInteractionService dbInteractionService;
    private Navigator navigator;
    private ArchiveHandler archiveHandler;

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
        this.navigator = navigator;
        this.archiveHandler = archiveHandler;

        this.loader.setImage(Icon.LOADING.toImage());
        loadingSpinner = new Spinner(this.loader);

        title.textProperty().bind(DisplayableText.of(IN_PROGRESS_TITLE).bindable());
        recordsLoaded.textProperty().bind(DisplayableText.of(IN_PROGRESS_MESSAGE).bindable());
        pathTitle.textProperty().bind(DisplayableText.of(PATH_TITLE).bindable());
        openLink.textProperty().bind(DisplayableText.of(OPEN_LINK).bindable());

        this.borderPane.setBottom(ButtonBox.create(
                ButtonBox.ButtonDescriber.builder()
                        .title(BUTTON_CANCEL)
                        .style(ButtonBox.ButtonStyle.SECONDARY)
                        .onAction(() -> dialogs.open(View.ARCHIVE_ABORT_DIALOG))
                        .build()
        ));

        this.openLink.setOnMouseClicked(event -> filesService.openInFileBrowser(userDefinedMetadata.getSaveAt()));
        this.archivePath.setText(userDefinedMetadata.getSaveAt().getAbsolutePath());
        this.subtitle1.setText(userDefinedMetadata.getDbName());

        downloadAndArchiveDatabase();
    }

    @Override
    public void destruct() {
        dbInteractionService.cancelRunning();
    }

    private void downloadAndArchiveDatabase() {
        loadingSpinner.play();

        dbInteractionService.execute(LoadDatabaseInstruction.builder()
                .connectionData(connectionData)
                .saveAt(Optional.of(userDefinedMetadata.getSaveAt()))
                .externalLobs(userDefinedMetadata.getLobFolder())
                .loadOnlyMetadata(false)
                .viewsAsTables(userDefinedMetadata.getExportViewsAsTables())
                .onSuccess(this::handleDownloadSuccess)
                .onFailure(event -> handleDownloadFailure(event.getSource().getException()))
                .onStepCompleted((observable, oldValue, newValue) -> {
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
        archiveHandler
                .write(archive, userDefinedMetadata)
                .save(archive, userDefinedMetadata.getSaveAt());

        log.info("Downloaded archive successfully stored to {}", userDefinedMetadata.getSaveAt().getAbsolutePath());

        loadingSpinner.hide();
        title.setVisible(false);
        fileSystemBox.setVisible(true);

        resultTitle.setVisible(true);
        resultTitle.getStyleClass().setAll("ok-circle-icon", "h2", "label-icon-left");
        resultTitle.textProperty().bind(DisplayableText.of(SUCCESS_TITLE).bindable());
        recordsLoaded.textProperty().bind(DisplayableText.of(SUCCESS_MESSAGE, total).bindable());

        progressBar.setVisible(false);
        scrollBox.setVisible(false);
        scrollBox2.setVisible(true);

        this.borderPane.setBottom(ButtonBox.create(
                ButtonBox.ButtonDescriber.builder()
                        .title(BUTTON_HOME)
                        .style(ButtonBox.ButtonStyle.PRIMARY)
                        .onAction(() -> navigator.navigate(View.START))
                        .build(),
                ButtonBox.ButtonDescriber.builder()
                        .title(BUTTON_VIEW_ARCHIVE)
                        .style(ButtonBox.ButtonStyle.SECONDARY)
                        .onAction(() -> {
                            val openArchive = archiveHandler.open(userDefinedMetadata.getSaveAt());
                            navigator.navigate(View.OPEN_SIARD_ARCHIVE_PREVIEW, openArchive);
                        })
                        .build()
        ));
    }

    private void handleDownloadFailure(final Throwable throwable) {
        this.title.setVisible(false);
        loadingSpinner.hide();
        progressBar.setVisible(false);
        this.resultTitle.setVisible(true);
        this.subtitle1.setVisible(false);
        this.resultBox.setVisible(false);
        resultTitle.textProperty().bind(DisplayableText.of(FAILED_TITLE).bindable());
        recordsLoaded.textProperty().bind(DisplayableText.of(FAILED_MESSAGE).bindable());
        resultTitle.getStyleClass().setAll("x-circle-icon", "h2", "label-icon-left");

        this.borderPane.setBottom(ButtonBox.create(
                ButtonBox.ButtonDescriber.builder()
                        .title(BUTTON_HOME)
                        .style(ButtonBox.ButtonStyle.PRIMARY)
                        .onAction(() -> navigator.navigate(View.START))
                        .build()
        ));

        errorHandler.handle(throwable);
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
            label.textProperty().bind(DisplayableText.of(TABLE_ROWS, text, rows).bindable());
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
