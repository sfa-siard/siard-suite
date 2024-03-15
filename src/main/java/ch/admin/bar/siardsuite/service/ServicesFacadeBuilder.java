package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.service.database.DbmsRegistry;
import ch.admin.bar.siardsuite.service.preferences.UserPreferences;
import ch.admin.bar.siardsuite.view.RootStage;
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

        return new ServicesFacade(
                stage,
                stage,
                Arrays.asList(
                        archiveHandler,
                        dbmsRegistry,
                        installationService,
                        userPreferences,
                        filesService,
                        dbInteractionService
                ));
    }
}
