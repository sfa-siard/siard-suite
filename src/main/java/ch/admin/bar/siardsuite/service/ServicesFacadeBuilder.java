package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.framework.ErrorHandler;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.service.database.DbmsRegistry;
import ch.admin.bar.siardsuite.service.preferences.UserPreferences;
import ch.admin.bar.siardsuite.ui.RootStage;
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

        val dbInteractionService = new DbInteractionService(archiveHandler);

        val errorHandler = new ShowErrorDialog();

        val services = new ServicesFacade(
                stage,
                stage,
                errorHandler,
                Arrays.asList(
                        archiveHandler,
                        dbmsRegistry,
                        installationService,
                        userPreferences,
                        filesService,
                        dbInteractionService
                ));

        errorHandler.setDialogs(services.dialogs());

        return services;
    }

    private final class ShowErrorDialog implements ErrorHandler {

        @Setter
        private Dialogs dialogs;

        @Override
        public void handle(Throwable e) {
            dialogs.open(View.ERROR, new Failure(e));
        }
    }
}
