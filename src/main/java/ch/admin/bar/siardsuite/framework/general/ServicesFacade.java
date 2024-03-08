package ch.admin.bar.siardsuite.framework.general;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.database.DbmsRegistry;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.view.ErrorHandler;
import ch.admin.bar.siardsuite.view.RootStage;
import lombok.Setter;

public class ServicesFacade {
    public static final ServicesFacade INSTANCE = new ServicesFacade();

    @Setter
    private RootStage rootStage;

    @Setter
    private Controller controller;

    public DbmsRegistry dbmsRegistry() {
        return new DbmsRegistry(); // TODO
    }

    public Navigator navigator() {
        return new Navigator(null, null); // TODO
    }

    public Dialogs dialogs() {
        return rootStage; // TODO
    }

    public DbInteractionService dbInteractionService() {
        return controller;
    }

    @Deprecated
    public Controller controller() {
        return controller; // TODO
    }

    public ErrorHandler errorHandler() {
        return rootStage; // TODO
    }
}
