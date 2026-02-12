package ch.admin.bar.siardsuite.framework.dialogs;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

@Value
public class SimpleShowDialogTarget {
    @NonNull Function<ServicesFacade, LoadedView> viewSupplier;
}
