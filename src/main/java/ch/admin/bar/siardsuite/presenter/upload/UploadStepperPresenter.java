package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.stepper.DrilledMFXStepper;
import ch.admin.bar.siardsuite.framework.general.DbInteractionService;
import ch.admin.bar.siardsuite.framework.general.Destructible;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepChain;
import ch.admin.bar.siardsuite.framework.steps.StepDefinition;
import ch.admin.bar.siardsuite.framework.steps.StepsChainBuilder;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.presenter.archive.ArchiveChooseDbmsPresenter;
import ch.admin.bar.siardsuite.presenter.archive.model.DbmsWithInitialValue;
import ch.admin.bar.siardsuite.presenter.upload.model.ArchiveAdder;
import ch.admin.bar.siardsuite.presenter.upload.model.ShowUploadResultsData;
import ch.admin.bar.siardsuite.presenter.upload.model.UploadArchiveData;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;

import java.util.Optional;

public class UploadStepperPresenter extends Presenter implements Destructible {

    private static final I18nKey SELECT_DBMS_TITLE = I18nKey.of("upload.step.name.dbms");
    private static final I18nKey DB_CONNECTION_TITLE = I18nKey.of("upload.step.name.databaseConnection");
    private static final I18nKey RESULT_TITLE = I18nKey.of("upload.step.name.result");

    private static final StepDefinition<Void, DbmsWithInitialValue> SELECT_DBMS =
            new StepDefinition<>(SELECT_DBMS_TITLE, ArchiveChooseDbmsPresenter::load);

    private static final StepDefinition<ArchiveAdder<DbmsWithInitialValue>, UploadArchiveData> EDIT_DB_CONNECTION_PROPERTIES =
            new StepDefinition<>(DB_CONNECTION_TITLE, UploadConnectionPresenter::load);

    private static final StepDefinition<ArchiveAdder<UploadArchiveData>, ShowUploadResultsData> UPLOAD_ARCHIVE =
            new StepDefinition<>(UploadingPresenter::load);

    private static final StepDefinition<ArchiveAdder<ShowUploadResultsData>, Void> UPLOAD_RESULTS =
            new StepDefinition<>(RESULT_TITLE, UploadResultPresenter::load);

    @FXML
    private DrilledMFXStepper stepper;

    private DbInteractionService dbInteractionService;
    private StepChain chain;

    @Override
    public void init(Controller controller, RootStage stage) {
        dbInteractionService = controller;

        chain = new StepsChainBuilder(
                ServicesFacade.INSTANCE,
                nextDisplayedStep -> stepper.display(nextDisplayedStep),
                nextDisplayedStep -> {
                    if (nextDisplayedStep.getDefinition().equals(UPLOAD_ARCHIVE)) {
                        // skip without displaying it
                        nextDisplayedStep.getNavigator().previous();
                    } else {
                        stepper.display(nextDisplayedStep);
                    }
                })
                .register(SELECT_DBMS)
                .transform(data -> new ArchiveAdder<>(controller.getSiardArchive().getArchive(), data))
                .register(EDIT_DB_CONNECTION_PROPERTIES)
                .transform(data -> new ArchiveAdder<>(controller.getSiardArchive().getArchive(), data))
                .register(UPLOAD_ARCHIVE)
                .transform(data -> new ArchiveAdder<>(controller.getSiardArchive().getArchive(), data))
                .register(UPLOAD_RESULTS)
                .build();

        stepper.init(chain.getSteps());

        controller.getRecentDatabaseConnection()
                .ifPresent(recentConnection -> {
                    // skip select dbms step
                    chain.getNavigatorOfStep(SELECT_DBMS)
                            .next(DbmsWithInitialValue.builder()
                                    .dbms(recentConnection.mapToDbmsConnectionData().getDbms())
                                    .initialValue(Optional.of(recentConnection))
                                    .build());
                });

    }

    @Override
    public void destruct() {
        stepper.destruct();
        chain.destruct();
        dbInteractionService.cancelRunning();
    }
}
