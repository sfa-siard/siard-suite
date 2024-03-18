package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.errors.FailureDisplay;
import ch.admin.bar.siardsuite.framework.errors.WarningDefinition;
import ch.admin.bar.siardsuite.service.database.DbmsRegistry;
import ch.admin.bar.siardsuite.service.preferences.UserPreferences;
import ch.admin.bar.siardsuite.ui.RootStage;
import ch.admin.bar.siardsuite.ui.View;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.Arrays;

public class ServicesFacadeBuilder {
    public ServicesFacade build(final RootStage stage) {
        val archiveHandler = new ArchiveHandler();
        val dbmsRegistry = new DbmsRegistry();
        val installationService = new InstallationService();
        val userPreferences = new UserPreferences();
        val filesService = new FilesService();
        val logService = new LogService();

        val dbInteractionService = new DbInteractionService(archiveHandler);

        val failureDisplay = new ShowDialogFailureDisplay();

        val services = new ServicesFacade(
                stage,
                stage,
                failureDisplay,
                Arrays.asList(
                        archiveHandler,
                        dbmsRegistry,
                        installationService,
                        userPreferences,
                        filesService,
                        dbInteractionService,
                        logService
                ));

        failureDisplay.setDialogs(services.dialogs());

        return services;
    }

    @Getter
    @Setter
    private static class ShowDialogFailureDisplay implements FailureDisplay {

        private Dialogs dialogs;

        @Override
        public void displayFailure(WarningDefinition failure) {
            dialogs.open(View.WARNING, failure);
        }
    }
}
