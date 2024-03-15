package ch.admin.bar.siardsuite.framework;

import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.view.RootStage;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Facade class for accessing various services in the application.
 */
@RequiredArgsConstructor
public class ServicesFacade {

    private final ViewDisplay viewDisplay;
    private final DialogDisplay dialogDisplay;

    private final List<Object> registeredServices;

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
        return new Navigator(viewDisplay, this);
    }

    /**
     * Returns the dialogs service for displaying various dialogs.
     */
    public Dialogs dialogs() {
        return new Dialogs(dialogDisplay, this);
    }

    /**
     * Returns the error handler for handling application errors.
     */
    public ErrorHandler errorHandler() {
        return dialogs().errorHandler();
    }
}
