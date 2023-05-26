package ch.admin.bar.siardsuite.component;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SiardLabelContainer {
    private final VBox container;

    public SiardLabelContainer(VBox container) {
        this.container = container;
    }

    public SiardLabelContainer withLabel(String value, String id) {
        Label label = new Label(value != null && !value.isEmpty() ? value : "-");
        label.setId(id);
        container.getChildren().add(label);
        return this;
    }
}
