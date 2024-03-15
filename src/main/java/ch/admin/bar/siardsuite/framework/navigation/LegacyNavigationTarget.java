package ch.admin.bar.siardsuite.framework.navigation;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.RootStage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

import java.util.function.BiFunction;

@Deprecated
@Value
@RequiredArgsConstructor
public class LegacyNavigationTarget {
    @NonNull BiFunction<Controller, RootStage, LoadedFxml<Presenter>> viewSupplier;

    public LegacyNavigationTarget(@NonNull final String fxmlLocation) {
        viewSupplier = (controller, stage) -> {
            val loadedFxml = FXMLLoadHelper.<Presenter>load(fxmlLocation);
            loadedFxml.getController().init(controller, stage);
            return loadedFxml;
        };
    }
}
