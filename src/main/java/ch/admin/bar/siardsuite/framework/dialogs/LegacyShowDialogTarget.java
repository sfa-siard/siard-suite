package ch.admin.bar.siardsuite.framework.dialogs;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.scene.Node;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.function.BiFunction;

@Deprecated
@Value
public class LegacyShowDialogTarget {
    @NonNull BiFunction<Controller, RootStage, Node> viewSupplier;

    public LegacyShowDialogTarget(@NonNull final String fxmlLocation) {
        viewSupplier = (controller, stage) -> {
            val loadedFxml = FXMLLoadHelper.<Presenter>load(fxmlLocation);
            loadedFxml.getController().init(controller, stage);
            return loadedFxml.getNode();
        };
    }
}