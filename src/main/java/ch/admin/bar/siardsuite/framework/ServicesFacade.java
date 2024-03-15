package ch.admin.bar.siardsuite.framework;

import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.util.CastHelper;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Facade class for accessing various services in the application.
 */
public class ServicesFacade {

    private final ErrorHandler errorHandler;

    private final List<Object> registeredServices;

    private final Navigator navigator;
    private final Dialogs dialogs;

    public ServicesFacade(ViewDisplay viewDisplay, DialogDisplay dialogDisplay, ErrorHandler errorHandler, List<Object> registeredServices) {
        this.errorHandler = errorHandler;
        this.registeredServices = registeredServices;

        this.navigator = new Navigator(viewDisplay, this);
        this.dialogs = new Dialogs(dialogDisplay, this);
    }

    public <T> T getService(final Class<T> serviceType) {
        val matchingServices = registeredServices.stream()
                .flatMap(CastHelper.tryCastInStream(serviceType))
                .collect(Collectors.toList());

        if (matchingServices.size() > 1) {
            throw new IllegalStateException(String.format(
                    "Multiple candidates for service-type %s available: %s",
                    serviceType.getCanonicalName(),
                    matchingServices.stream()
                            .map(candidate -> candidate.getClass().getCanonicalName())
                            .collect(Collectors.joining("\n - "))));
        }

        if (matchingServices.isEmpty()) {
            throw new IllegalStateException(String.format(
                    "No candidate for service-type %s available",
                    serviceType.getCanonicalName()));
        }

        return matchingServices.get(0);
    }

    /**
     * Returns the navigator for navigation within the application.
     */
    public Navigator navigator() {
        return navigator;
    }

    /**
     * Returns the dialogs service for displaying various dialogs.
     */
    public Dialogs dialogs() {
        return dialogs;
    }

    /**
     * Returns the error handler for handling application errors.
     */
    public ErrorHandler errorHandler() {
        return errorHandler;
    }
}
