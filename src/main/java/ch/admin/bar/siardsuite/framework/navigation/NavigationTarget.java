package ch.admin.bar.siardsuite.framework.navigation;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import lombok.NonNull;
import lombok.Value;

import java.util.function.BiFunction;

@Value
public class NavigationTarget<T> {
    @NonNull BiFunction<T, ServicesFacade, LoadedFxml> viewSupplier;
}
