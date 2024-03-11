package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.component.IconView;
import ch.admin.bar.siardsuite.component.LabelIcon;
import ch.admin.bar.siardsuite.component.Spinner;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.database.model.UploadDatabaseInstruction;
import ch.admin.bar.siardsuite.framework.general.DbInteractionService;
import ch.admin.bar.siardsuite.framework.general.Dialogs;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.presenter.archive.model.DbmsWithInitialValue;
import ch.admin.bar.siardsuite.presenter.upload.model.ArchiveAdder;
import ch.admin.bar.siardsuite.presenter.upload.model.ShowUploadResultsData;
import ch.admin.bar.siardsuite.presenter.upload.model.UploadArchiveData;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.ErrorHandler;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.val;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.model.View.UPLOAD_ABORT_DIALOG;

public class UploadingPresenter {
    @FXML
    public Label title;
    @FXML
    public ImageView loader;
    @FXML
    public Label progress;
    @FXML
    public MFXButton cancel;
    @FXML
    public VBox scrollBox;
    @FXML
    public MFXProgressBar progressBar;

    public void init(
            final DbmsConnectionData dbmsConnectionData,
            final Archive archive,
            final Map<String, String> schemaNameMappings,
            final StepperNavigator<ShowUploadResultsData> navigator,
            final ErrorHandler errorHandler,
            final DbInteractionService dbInteractionService,
            final Dialogs dialogs
    ) {
        this.loader.setImage(Icon.loading);
        Spinner loadingSpinner = new Spinner(this.loader);
        loadingSpinner.play();
        I18n.bind(title.textProperty(), "upload.inProgress.title");
        I18n.bind(progress.textProperty(), "upload.records.uploaded.message");
        I18n.bind(cancel.textProperty(), "button.cancel");

        cancel.setOnAction(event -> dialogs.openDialog(UPLOAD_ABORT_DIALOG)); // TODO: how to cancel the upload task

        scrollBox.getChildren().clear();

        try {
            dbInteractionService.execute(UploadDatabaseInstruction.builder()
                    .connectionData(dbmsConnectionData)
                    .archive(archive)
                    .schemaNameMappings(schemaNameMappings)
                    .onSuccess(event -> navigator.next(ShowUploadResultsData.builder()
                            .build()))
                    .onFailure(event -> {
                        navigator.next(ShowUploadResultsData.builder()
                                .failure(Optional.of(new Failure(event.getSource().getException())))
                                .build());
                    })
                    .onProgress((observable, oldValue, newValue) -> {
                        double pos = newValue.doubleValue();
                        progressBar.progressProperty().set(pos);
                    })
                    .onStepCompleted((observable, oldValue, newValue) -> {
                        AtomicInteger pos1 = new AtomicInteger();
                        addLoadingData(newValue, pos1.getAndIncrement());
                    })
                    .build());
        } catch (Exception e) {
            errorHandler.handle(e);
        }
    }

    private void addLoadingData(String text, Integer pos) {
        // set previous to ok
        if (scrollBox.getChildren().size() > 0) {
            int itemPos = scrollBox.getChildren().size() - 1;
            LabelIcon label = (LabelIcon) scrollBox.getChildren().get(itemPos);
            label.setGraphic(new IconView(itemPos, IconView.IconType.OK));
        }
        scrollBox.getChildren().add(
                new LabelIcon(text, pos, IconView.IconType.LOADING));
    }

    public static LoadedFxml<UploadingPresenter> load(
            final ArchiveAdder<UploadArchiveData> data,
            final StepperNavigator<ShowUploadResultsData> navigator,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<UploadingPresenter>load("fxml/upload/upload-uploading.fxml");
        loaded.getController().init(
                data.getData().getConnectionData(),
                data.getArchive(),
                data.getData().getSchemaNameMappings(),
                navigator,
                servicesFacade.errorHandler(),
                servicesFacade.dbInteractionService(),
                servicesFacade.dialogs());

        return loaded;
    }
}
