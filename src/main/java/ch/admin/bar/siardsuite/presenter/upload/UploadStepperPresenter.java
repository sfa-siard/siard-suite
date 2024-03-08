package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.stepper.StepperInitializer;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepDefinition;
import ch.admin.bar.siardsuite.framework.steps.StepDefinitionWithContext;
import ch.admin.bar.siardsuite.framework.steps.StepsChainBuilder;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.presenter.archive.ArchiveChooseDbmsPresenter;
import ch.admin.bar.siardsuite.presenter.archive.model.DbmsWithInitialValue;
import ch.admin.bar.siardsuite.presenter.upload.model.ShowUploadResultsData;
import ch.admin.bar.siardsuite.presenter.upload.model.UploadArchiveData;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import lombok.val;

import java.util.Optional;

public class UploadStepperPresenter extends Presenter {

    private static final I18nKey SELECT_DBMS_TITLE = I18nKey.of("upload.step.name.dbms");
    private static final I18nKey DB_CONNECTION_TITLE = I18nKey.of("upload.step.name.databaseConnection");
    private static final I18nKey RESULT_TITLE = I18nKey.of("upload.step.name.result");

    private static final StepDefinition<Void, DbmsWithInitialValue> SELECT_DBMS =
            StepDefinition.<Void, DbmsWithInitialValue>builder()
                    .title(SELECT_DBMS_TITLE)
                    .inputType(Void.class)
                    .outputType(DbmsWithInitialValue.class)
                    .viewLoader(ArchiveChooseDbmsPresenter::load) // TODO: Rename and move
                    .build();

    private static final StepDefinitionWithContext<DbmsWithInitialValue, UploadArchiveData, Archive> EDIT_DB_CONNECTION_PROPERTIES =
    StepDefinitionWithContext.<DbmsWithInitialValue, UploadArchiveData, Archive>builder()
                    .title(DB_CONNECTION_TITLE)
                    .inputType(DbmsWithInitialValue.class)
                    .outputType(UploadArchiveData.class)
                    .viewLoader(UploadConnectionPresenter::load)
                    .build();

    private static final StepDefinitionWithContext<UploadArchiveData, ShowUploadResultsData, Archive> UPLOAD_ARCHIVE =
            StepDefinitionWithContext.<UploadArchiveData, ShowUploadResultsData, Archive>builder()
                    .inputType(UploadArchiveData.class)
                    .outputType(ShowUploadResultsData.class)
                    .viewLoader(UploadingPresenter::load)
                    .build();

    private static final StepDefinitionWithContext<ShowUploadResultsData, Void, Archive> UPLOAD_RESULTS = StepDefinitionWithContext.<ShowUploadResultsData, Void, Archive>builder()
            .title(RESULT_TITLE)
            .inputType(ShowUploadResultsData.class)
            .outputType(Void.class)
            .viewLoader(UploadResultPresenter::load)
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

                    if (nextDisplayedStep.getId().equals(UPLOAD_ARCHIVE.getId())) {
                        stepper.previous();
                    }
                },
                controller.getSiardArchive().getArchive())
                .register(SELECT_DBMS)
                .register(EDIT_DB_CONNECTION_PROPERTIES)
                .register(UPLOAD_ARCHIVE)
                .register(UPLOAD_RESULTS)
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
}
