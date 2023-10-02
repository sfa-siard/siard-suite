package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.presenter.tree.DetailsPresenter;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.val;

import java.util.stream.Collectors;

public class FormRenderer extends DetailsPresenter {

    private static final String TITLE_STYLE_CLASS = "table-container-label";
    private static final String READONLY_VALUE_STYLE_CLASS = "table-container-text";

    @FXML
    private VBox container;

    @Getter
    private BooleanProperty changed = new SimpleBooleanProperty(false);

    @Override
    public void init(Controller controller, RootStage stage, TreeAttributeWrapper wrapper) {
        val renderableForm = wrapper.getRenderableForm()
                .orElseThrow(() -> new IllegalArgumentException("No renderable form provided"));

        controller.getSiardArchive().getMetaData();

        renderForm(renderableForm);
    }

    private void renderForm(final RenderableForm renderableForm) {
        val renderedGroups = renderableForm.getGroups().stream()
                .map(this::createGroup)
                .collect(Collectors.toList());

        container.getChildren().setAll(renderedGroups);
    }

    private VBox createGroup(final RenderableFormGroup group) {
        val vbox = new VBox();
        vbox.getChildren().setAll(
                group.getProperties().stream()
                        .map(renderableProperty -> {
                            if (renderableProperty instanceof ReadWriteStringProperty) {
                                return createField((ReadWriteStringProperty) renderableProperty);
                            }

                            if (renderableProperty instanceof ReadOnlyStringProperty) {
                                return createField((ReadOnlyStringProperty) renderableProperty);
                            }

                            throw new IllegalArgumentException(String.format(
                                    "Property type %s ins not supported yet.",
                                    renderableProperty.getClass().getName()
                            ));
                        })
                        .collect(Collectors.toList()));

        return vbox;
    }

    private VBox createField(final ReadWriteStringProperty property) {
        val vbox = new VBox();

        val titleLabel = new Label();
        titleLabel.setText(property.getTitle());
        titleLabel.getStyleClass().add(TITLE_STYLE_CLASS);

        val valueTextField = new TextField();
        valueTextField.setText(property.getValue());
        valueTextField.getStyleClass().add(READONLY_VALUE_STYLE_CLASS); // TODO own style class

        final ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
            this.changed.set(true);
        };
        valueTextField.textProperty().addListener(changeListener);

        vbox.getChildren().setAll(titleLabel, valueTextField);

        return vbox;
    }

    private VBox createField(final ReadOnlyStringProperty property) {
        val vbox = new VBox();

        val titleLabel = new Label();
        titleLabel.setText(property.getTitle());
        titleLabel.getStyleClass().add(TITLE_STYLE_CLASS);

        val valueLabel = new Label();
        valueLabel.setText(property.getValue());
        valueLabel.getStyleClass().add(READONLY_VALUE_STYLE_CLASS);

        vbox.getChildren().setAll(titleLabel, valueLabel);

        return vbox;
    }

    @Override
    protected void bindLabels() {

    }
}
