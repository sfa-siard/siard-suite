package ch.admin.bar.siardsuite.framework.general;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.database.DbmsRegistry;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.service.ArchiveHandler;
import ch.admin.bar.siardsuite.service.DbInteractionService;
import ch.admin.bar.siardsuite.view.ErrorHandler;
import ch.admin.bar.siardsuite.view.RootStage;
import lombok.Setter;

/**
 * Facade class for accessing various services in the application.
 */
public class ServicesFacade {
    public static final ServicesFacade INSTANCE = new ServicesFacade();

    @Setter
    private RootStage rootStage;

    @Setter
    private Controller controller;

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
        return new Navigator(controller, rootStage);
    }

    /**
     * Returns the dialogs service for displaying various dialogs.
     */
    public Dialogs dialogs() {
        return new Dialogs(controller, rootStage);
    }

    /**
     * Returns the database interaction service.
     */
    public DbInteractionService dbInteractionService() {
        return dbInteractionService;
    }

    /**
     * Returns the legacy controller instance.
     *
     * @deprecated This method is deprecated and should not be used. It exists only for compatibility
     * reasons  and will be removed in further releases.
     */
    @Deprecated
    public Controller controller() {
        return controller;
    }

    /**
     * Returns the error handler for handling application errors.
     */
    public ErrorHandler errorHandler() {
        return rootStage;
    }


    public ArchiveHandler archiveHandler() {
        return new ArchiveHandler();
    }
}
