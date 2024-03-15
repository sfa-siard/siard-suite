package ch.admin.bar.siardsuite.framework.general;

import ch.admin.bar.siardsuite.database.DbmsRegistry;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.presenter.ErrorDialogPresenter;
import ch.admin.bar.siardsuite.service.ArchiveHandler;
import ch.admin.bar.siardsuite.service.DbInteractionService;
import ch.admin.bar.siardsuite.service.InstallationService;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import ch.admin.bar.siardsuite.view.ErrorHandler;
import ch.admin.bar.siardsuite.view.RootStage;
import lombok.Setter;
import lombok.val;

/**
 * Facade class for accessing various services in the application.
 */
public class ServicesFacade {
    public static final ServicesFacade INSTANCE = new ServicesFacade();

    @Setter
    private RootStage rootStage;

    private final ArchiveHandler archiveHandler = new ArchiveHandler();
    private final DbInteractionService dbInteractionService = new DbInteractionService(archiveHandler);

    /**
     * Returns the database management system registry.
     */
    public DbmsRegistry dbmsRegistry() {
        return new DbmsRegistry();
    }

    /**
     * Returns the navigator for navigation within the application.
     */
    public Navigator navigator() {
        return new Navigator(rootStage);
    }

    /**
     * Returns the dialogs service for displaying various dialogs.
     */
    public Dialogs dialogs() {
        return new Dialogs(rootStage);
    }

    /**
     * Returns the database interaction service.
     */
    public DbInteractionService dbInteractionService() {
        return dbInteractionService;
    }

    /**
     * Returns the error handler for handling application errors.
     */
    public ErrorHandler errorHandler() {
        return dialogs().errorHandler();
    }

    public ArchiveHandler archiveHandler() {
        return new ArchiveHandler();
    }

    public InstallationService installationService() {
        return new InstallationService();
    }

    public UserPreferences userPreferences() {
        return UserPreferences.INSTANCE; // TODO
    }
}
