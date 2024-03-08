package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.stepper.StepperInitializer;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepDefinition;
import ch.admin.bar.siardsuite.framework.steps.StepsChainBuilder;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.presenter.archive.model.DbmsWithInitialValue;
import ch.admin.bar.siardsuite.presenter.archive.model.SiardArchiveWithConnectionData;
import ch.admin.bar.siardsuite.presenter.archive.model.UserDefinedMetadata;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import lombok.val;

import java.util.Optional;

public class ArchiveStepperPresenter extends Presenter {

    private static final I18nKey SELECT_DBMS_TITLE = I18nKey.of("archive.step.name.dbms");
    private static final I18nKey DB_CONNECTION_TITLE = I18nKey.of("archive.step.name.databaseConnectionURL");
    private static final I18nKey DB_PREVIEW_TITLE = I18nKey.of("archive.step.name.preview");
    private static final I18nKey EDIT_META_DATA_TITLE = I18nKey.of("archive.step.name.metadata");
    private static final I18nKey DB_DOWNLOAD_TITLE = I18nKey.of("archive.step.name.download");

    private static final StepDefinition<Void, DbmsWithInitialValue> SELECT_DBMS = StepDefinition.<Void, DbmsWithInitialValue>builder()
            .title(SELECT_DBMS_TITLE)
            .inputType(Void.class)
            .outputType(DbmsWithInitialValue.class)
            .viewLoader(ArchiveChooseDbmsPresenter::load)
            .build();

    private static final StepDefinition<DbmsWithInitialValue, DbmsConnectionData> EDIT_DB_CONNECTION_PROPERTIES = StepDefinition.<DbmsWithInitialValue, DbmsConnectionData>builder()
            .title(DB_CONNECTION_TITLE)
            .inputType(DbmsWithInitialValue.class)
            .outputType(DbmsConnectionData.class)
            .viewLoader(ArchiveConnectionPresenter::load)
            .build();

    private static final StepDefinition<DbmsConnectionData, SiardArchiveWithConnectionData> DOWNLOAD_METADATA = StepDefinition.<DbmsConnectionData, SiardArchiveWithConnectionData>builder() // TODO not visible in header
            .inputType(DbmsConnectionData.class)
            .outputType(SiardArchiveWithConnectionData.class)
            .viewLoader(ArchiveLoadingPreviewPresenter::load)
            .build();

    private static final StepDefinition<SiardArchiveWithConnectionData, SiardArchiveWithConnectionData> PREVIEW_METADATA = StepDefinition.<SiardArchiveWithConnectionData, SiardArchiveWithConnectionData>builder()
            .title(DB_PREVIEW_TITLE)
            .inputType(SiardArchiveWithConnectionData.class)
            .outputType(SiardArchiveWithConnectionData.class)
            .viewLoader(PreviewArchiveBrowser::load)
            .build();

    private static final StepDefinition<SiardArchiveWithConnectionData, UserDefinedMetadata> EDIT_USER_DEFINED_METADATA = StepDefinition.<SiardArchiveWithConnectionData, UserDefinedMetadata>builder()
            .title(EDIT_META_DATA_TITLE)
            .inputType(SiardArchiveWithConnectionData.class)
            .outputType(UserDefinedMetadata.class)
            .viewLoader(ArchiveMetaDataEditorPresenter::load)
            .build();

    private static final StepDefinition<UserDefinedMetadata, Void> DOWNLOAD_DB = StepDefinition.<UserDefinedMetadata, Void>builder()
            .title(DB_DOWNLOAD_TITLE)
            .inputType(UserDefinedMetadata.class)
            .outputType(Void.class)
            .viewLoader(ArchiveDownloadPresenter::load)
            .build();

    @FXML
    private MFXStepper stepper;

    @Override
    public void init(Controller controller, RootStage stage) {
        val chain = new StepsChainBuilder<>(
                ServicesFacade.INSTANCE,
                nextDisplayedStep -> stepper.next(),
                nextDisplayedStep -> {
                    stepper.previous();

                    if (nextDisplayedStep.getId().equals(DOWNLOAD_METADATA.getId())) {
                        stepper.previous();
                    }
                })
                .register(SELECT_DBMS)
                .register(EDIT_DB_CONNECTION_PROPERTIES)
                .register(DOWNLOAD_METADATA)
                .register(PREVIEW_METADATA)
                .register(EDIT_USER_DEFINED_METADATA)
                .register(DOWNLOAD_DB)
                .build();

        new StepperInitializer(stage, stepper)
                .init(chain.getSteps());

        controller.getRecentDatabaseConnection()
                .ifPresent(recentConnection -> {
                    // skip select dbms step
                    chain.getNavigatorOfStep(SELECT_DBMS.getId())
                            .next(DbmsWithInitialValue.builder()
                                    .dbms(recentConnection.mapToDbmsConnectionData().getDbms())
                                    .initialValue(Optional.of(recentConnection))
                                    .build());
                });
    }

    public static LoadedFxml<ArchiveStepperPresenter> load(Controller controller, RootStage stage) {
        val loaded = FXMLLoadHelper.<ArchiveStepperPresenter>load("fxml/archive/archive-stepper.fxml");
        loaded.getController().init(controller, stage);

        return loaded;
    }
}
