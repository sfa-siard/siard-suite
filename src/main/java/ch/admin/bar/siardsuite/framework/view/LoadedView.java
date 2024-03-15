package ch.admin.bar.siardsuite.framework.view;

import javafx.scene.Node;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Supplier;

@Value
public class LoadedView<C> {
    @NonNull Supplier<Node> nodeSupplier;
    @NonNull C controller;

    public LoadedView(@NonNull Node node, @NonNull C controller) {
        this.nodeSupplier = () -> node;
        this.controller = controller;
    }

    public LoadedView(@NonNull Supplier<Node> nodeSupplier, @NonNull C controller) {
        this.nodeSupplier = nodeSupplier;
        this.controller = controller;
    }

    public <N extends Node> N getNode() {
        return (N)nodeSupplier.get();
    }
}
