package ch.admin.bar.siardsuite.framework.navigation;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import lombok.NonNull;
import lombok.Value;

import java.util.function.BiFunction;

@Value
public class NavigationTarget<T> {
    @NonNull BiFunction<T, ServicesFacade, LoadedView> viewSupplier;
}
