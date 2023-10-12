package ch.admin.bar.siardsuite.util.fxml;

import javafx.scene.Node;
import lombok.NonNull;
import lombok.Value;

@Value
public class LoadedFxml<C> {
    @NonNull Node node;
    @NonNull C controller;
}
