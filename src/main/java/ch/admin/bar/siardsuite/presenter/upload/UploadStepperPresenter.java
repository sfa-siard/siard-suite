package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.component.stepper.DrilledMFXStepper;
import ch.admin.bar.siardsuite.framework.Destructible;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepChain;
import ch.admin.bar.siardsuite.framework.steps.StepDefinition;
import ch.admin.bar.siardsuite.framework.steps.StepsChainBuilder;
import ch.admin.bar.siardsuite.model.Tuple;
import ch.admin.bar.siardsuite.presenter.archive.ArchiveChooseDbmsPresenter;
import ch.admin.bar.siardsuite.presenter.archive.model.DbmsWithInitialValue;
import ch.admin.bar.siardsuite.presenter.upload.model.ArchiveAdder;
import ch.admin.bar.siardsuite.presenter.upload.model.ShowUploadResultsData;
import ch.admin.bar.siardsuite.presenter.upload.model.UploadArchiveData;
import ch.admin.bar.siardsuite.service.DbInteractionService;
import ch.admin.bar.siardsuite.service.preferences.RecentDbConnection;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import javafx.fxml.FXML;
import lombok.val;

import java.util.Optional;

public class UploadStepperPresenter implements Destructible {

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

    public void init(
            final Archive archive,
            final Optional<RecentDbConnection> recentDbConnection,
            final DbInteractionService dbInteractionService,
            final ServicesFacade servicesFacade
    ) {
        this.dbInteractionService = dbInteractionService;

        chain = new StepsChainBuilder(
                servicesFacade,
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
                .transform(data -> new ArchiveAdder<>(archive, data))
                .register(EDIT_DB_CONNECTION_PROPERTIES)
                .transform(data -> new ArchiveAdder<>(archive, data))
                .register(UPLOAD_ARCHIVE)
                .transform(data -> new ArchiveAdder<>(archive, data))
                .register(UPLOAD_RESULTS)
                .build();

        stepper.init(chain.getSteps());

        recentDbConnection.ifPresent(recentConnection -> {
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

    public static LoadedView<UploadStepperPresenter> load(
            final Archive data,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<UploadStepperPresenter>load("fxml/upload/upload-stepper.fxml");
        loaded.getController().init(
                data,
                Optional.empty(),
                servicesFacade.getService(DbInteractionService.class),
                servicesFacade
        );

        return loaded;
    }

    public static LoadedView<UploadStepperPresenter> loadWithRecentConnection(
            final Tuple<Archive, RecentDbConnection> data,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<UploadStepperPresenter>load("fxml/upload/upload-stepper.fxml");
        loaded.getController().init(
                data.getValue1(),
                Optional.of(data.getValue2()),
                servicesFacade.getService(DbInteractionService.class),
                servicesFacade
        );

        return loaded;
    }
}
