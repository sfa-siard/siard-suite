package ch.admin.bar.siardsuite.util.fxml;

import javafx.scene.Node;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Supplier;

@Value
public class LoadedFxml<C> {
    @NonNull Supplier<Node> nodeSupplier;
    @NonNull C controller;

    public LoadedFxml(@NonNull Node node, @NonNull C controller) {
        this.nodeSupplier = () -> node;
        this.controller = controller;
    }

    public LoadedFxml(@NonNull Supplier<Node> nodeSupplier, @NonNull C controller) {
        this.nodeSupplier = nodeSupplier;
        this.controller = controller;
    }

    public <N extends Node> N getNode() {
        return (N)nodeSupplier.get();
    }
}
