package ch.admin.bar.siardsuite.ui.presenter.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.ui.component.stepper.DrilledMFXStepper;
import ch.admin.bar.siardsuite.framework.hooks.Destructible;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepChain;
import ch.admin.bar.siardsuite.framework.steps.StepDefinition;
import ch.admin.bar.siardsuite.framework.steps.StepsChainBuilder;
import ch.admin.bar.siardsuite.model.Tuple;
import ch.admin.bar.siardsuite.model.UserDefinedMetadata;
import ch.admin.bar.siardsuite.ui.presenter.archive.model.DbmsWithInitialValue;
import ch.admin.bar.siardsuite.service.DbInteractionService;
import ch.admin.bar.siardsuite.service.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.service.preferences.RecentDbConnection;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import javafx.fxml.FXML;
import lombok.val;

import java.util.Optional;

public class ArchiveStepperPresenter implements Destructible {

    private static final I18nKey SELECT_DBMS_TITLE = I18nKey.of("archive.step.name.dbms");
    private static final I18nKey DB_CONNECTION_TITLE = I18nKey.of("archive.step.name.databaseConnectionURL");
    private static final I18nKey DB_PREVIEW_TITLE = I18nKey.of("archive.step.name.preview");
    private static final I18nKey EDIT_META_DATA_TITLE = I18nKey.of("archive.step.name.metadata");
    private static final I18nKey DB_DOWNLOAD_TITLE = I18nKey.of("archive.step.name.download");

    private static final StepDefinition<Void, DbmsWithInitialValue> SELECT_DBMS =
            new StepDefinition<>(SELECT_DBMS_TITLE, ArchiveChooseDbmsPresenter::load);

    private static final StepDefinition<DbmsWithInitialValue, DbmsConnectionData> EDIT_DB_CONNECTION_PROPERTIES =
            new StepDefinition<>(DB_CONNECTION_TITLE, ArchiveConnectionPresenter::load);

    private static final StepDefinition<DbmsConnectionData, Tuple<Archive, DbmsConnectionData>> DOWNLOAD_METADATA =
            new StepDefinition<>(ArchiveLoadingPreviewPresenter::load);

    private static final StepDefinition<Tuple<Archive, DbmsConnectionData>, Tuple<Archive, DbmsConnectionData>> PREVIEW_METADATA =
            new StepDefinition<>(DB_PREVIEW_TITLE, PreviewArchiveBrowser::load);

    private static final StepDefinition<Tuple<Archive, DbmsConnectionData>, Tuple<UserDefinedMetadata, DbmsConnectionData>> EDIT_USER_DEFINED_METADATA =
            new StepDefinition<>(EDIT_META_DATA_TITLE, ArchiveMetaDataEditorPresenter::load);

    private static final StepDefinition<Tuple<UserDefinedMetadata, DbmsConnectionData>, Void> DOWNLOAD_DB =
            new StepDefinition<>(DB_DOWNLOAD_TITLE, ArchiveDownloadPresenter::load);

    @FXML
    private DrilledMFXStepper stepper;

    private DbInteractionService dbInteractionService;
    private StepChain chain;

    public void init(
            final Optional<RecentDbConnection> recentDbConnection,
            final DbInteractionService dbInteractionService,
            final ServicesFacade servicesFacade
    ) {
        this.dbInteractionService = dbInteractionService;

        chain = new StepsChainBuilder(
                servicesFacade,
                nextDisplayedStep -> stepper.display(nextDisplayedStep),
                nextDisplayedStep -> {
                    if (nextDisplayedStep.getDefinition().equals(DOWNLOAD_METADATA)) {
                        // skip without displaying it
                        nextDisplayedStep.getNavigator().previous();
                    } else {
                        stepper.display(nextDisplayedStep);
                    }
                })
                .register(SELECT_DBMS)
                .register(EDIT_DB_CONNECTION_PROPERTIES)
                .register(DOWNLOAD_METADATA)
                .register(PREVIEW_METADATA)
                .register(EDIT_USER_DEFINED_METADATA)
                .register(DOWNLOAD_DB)
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

    public static LoadedView<ArchiveStepperPresenter> load(
            final Optional<RecentDbConnection> data,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<ArchiveStepperPresenter>load("fxml/archive/archive-stepper.fxml");
        loaded.getController().init(
                data,
                servicesFacade.getService(DbInteractionService.class),
                servicesFacade
        );

        return loaded;
    }

    @Override
    public void destruct() {
        stepper.destruct();
        chain.destruct();
        dbInteractionService.cancelRunning();
    }
}
