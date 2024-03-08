package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepDefinition;
import ch.admin.bar.siardsuite.framework.steps.StepsChainBuilder;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.presenter.archive.model.SiardArchiveWithConnectionData;
import ch.admin.bar.siardsuite.presenter.archive.model.UserDefinedMetadata;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;

import java.util.List;

public class ArchiveStepperPresenter extends StepperPresenter {

    private static final I18nKey SELECT_DBMS_TITLE = I18nKey.of("archive.step.name.dbms");
    private static final I18nKey DB_CONNECTION_TITLE = I18nKey.of("archive.step.name.databaseConnectionURL");
    private static final I18nKey DB_PREVIEW_TITLE = I18nKey.of("archive.step.name.preview");
    private static final I18nKey EDIT_META_DATA_TITLE = I18nKey.of("archive.step.name.metadata");
    private static final I18nKey DB_DOWNLOAD_TITLE = I18nKey.of("archive.step.name.download");

    private final List<StepsChainBuilder.StepMetaData> steps = new StepsChainBuilder(this::display, ServicesFacade.INSTANCE)
            .register(StepDefinition.<Void, Dbms>builder()
                    .title(DisplayableText.of(SELECT_DBMS_TITLE))
                    .inputType(Void.class)
                    .outputType(Dbms.class)
                    .viewLoader(ArchiveChooseDbmsPresenter::load)
                    .build())
            .register(StepDefinition.<Dbms, DbmsConnectionData>builder()
                    .title(DisplayableText.of(DB_CONNECTION_TITLE))
                    .inputType(Dbms.class)
                    .outputType(DbmsConnectionData.class)
                    .viewLoader(ArchiveConnectionPresenter::load)
                    .build())
            .register(StepDefinition.<DbmsConnectionData, SiardArchiveWithConnectionData>builder() // TODO not visible in header
                    .inputType(DbmsConnectionData.class)
                    .outputType(SiardArchiveWithConnectionData.class)
                    .viewLoader(ArchiveLoadingPreviewPresenter::load)
                    .build())
            .register(StepDefinition.<SiardArchiveWithConnectionData, SiardArchiveWithConnectionData>builder()
                    .title(DisplayableText.of(DB_PREVIEW_TITLE))
                    .inputType(SiardArchiveWithConnectionData.class)
                    .outputType(SiardArchiveWithConnectionData.class)
                    .viewLoader(PreviewArchiveBrowser::load)
                    .build())
            .register(StepDefinition.<SiardArchiveWithConnectionData, UserDefinedMetadata>builder()
                    .title(DisplayableText.of(EDIT_META_DATA_TITLE))
                    .inputType(SiardArchiveWithConnectionData.class)
                    .outputType(UserDefinedMetadata.class)
                    .viewLoader(ArchiveMetaDataEditorPresenter::load)
                    .build())
            .register(StepDefinition.<UserDefinedMetadata, Void>builder()
                    .title(DisplayableText.of(DB_DOWNLOAD_TITLE))
                    .inputType(UserDefinedMetadata.class)
                    .outputType(Void.class)
                    .viewLoader(ArchiveDownloadPresenter::load)
                    .build())
            .build();

    @FXML
    private MFXStepper stepper;

    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

        createStepper(ArchiveSteps.steps, stepper);
    }

    @Override
    public void init(Controller controller, RootStage stage, MFXStepper stepper) {
    }

    public void display(final LoadedFxml loadedFxml) {

    }
}
