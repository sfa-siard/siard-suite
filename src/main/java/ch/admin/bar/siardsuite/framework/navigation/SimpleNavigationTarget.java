package ch.admin.bar.siardsuite.framework.navigation;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

@Value
public class SimpleNavigationTarget {
    @NonNull Function<ServicesFacade, LoadedView> viewSupplier;
}
