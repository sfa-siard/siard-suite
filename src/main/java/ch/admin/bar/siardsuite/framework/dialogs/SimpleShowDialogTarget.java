package ch.admin.bar.siardsuite.framework.dialogs;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

@Value
public class SimpleShowDialogTarget {
    @NonNull Function<ServicesFacade, LoadedFxml> viewSupplier;
}
