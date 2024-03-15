package ch.admin.bar.siardsuite.framework.dialogs;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import lombok.NonNull;
import lombok.Value;

import java.util.function.BiFunction;
import java.util.function.Function;

@Value
public class ShowDialogTarget<T> {
    @NonNull BiFunction<T, ServicesFacade, LoadedView> viewSupplier;

    public ShowDialogTarget(@NonNull BiFunction<T, ServicesFacade, LoadedView> viewSupplier) {
        this.viewSupplier = viewSupplier;
    }

    public ShowDialogTarget(@NonNull Function<T, LoadedView> viewSupplier) {
        this.viewSupplier = (data, servicesFacade) -> viewSupplier.apply(data);
    }
}
