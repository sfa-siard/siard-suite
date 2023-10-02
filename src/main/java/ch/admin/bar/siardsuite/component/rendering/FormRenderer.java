package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.presenter.tree.ChangeableDataPresenter;
import ch.admin.bar.siardsuite.presenter.tree.DetailsPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.val;

import java.util.stream.Collectors;

public class FormRenderer extends DetailsPresenter implements ChangeableDataPresenter {

    private static final String TITLE_STYLE_CLASS = "table-container-label";
    private static final String READONLY_VALUE_STYLE_CLASS = "table-container-text";

    @FXML
    private VBox container;


    private BooleanProperty hasChanged = new SimpleBooleanProperty(false);

    @Override
    public void init(Controller controller, RootStage stage, TreeAttributeWrapper wrapper) {
        val renderableForm = wrapper.getRenderableForm()
                .orElseThrow(() -> new IllegalArgumentException("No renderable form provided"));

        val data = renderableForm.getDataExtractor().apply(controller);

        controller.getSiardArchive();

        renderForm(renderableForm, data);
    }

    private <T> void renderForm(final RenderableForm<T> renderableForm, final T data) {
        val renderedGroups = renderableForm.getGroups().stream()
                .map(renderableGroup -> createGroup(renderableGroup, data))
                .collect(Collectors.toList());

        container.getChildren().setAll(renderedGroups);
    }

    private <T> VBox createGroup(final RenderableFormGroup<T> group, final T data) {
        val vbox = new VBox();
        vbox.getChildren().setAll(
                group.getProperties().stream()
                        .map(renderableProperty -> {
                            if (renderableProperty instanceof ReadWriteStringProperty) {
                                return createField((ReadWriteStringProperty<T>) renderableProperty, data);
                            }

                            if (renderableProperty instanceof ReadOnlyStringProperty) {
                                return createField((ReadOnlyStringProperty<T>) renderableProperty, data);
                            }

                            throw new IllegalArgumentException(String.format(
                                    "Property type %s ins not supported yet.",
                                    renderableProperty.getClass().getName()
                            ));
                        })
                        .collect(Collectors.toList()));

        return vbox;
    }

    private <T> VBox createField(final ReadWriteStringProperty<T> property, final T data) {
        val vbox = new VBox();

        val titleLabel = new Label();
        titleLabel.setText(I18n.get(property.getTitle()));
        titleLabel.getStyleClass().add(TITLE_STYLE_CLASS);

        val valueTextField = new TextField();

        val value = property.getPropertyExtractor().apply(data).get();
        valueTextField.setText(value);
        valueTextField.getStyleClass().add(READONLY_VALUE_STYLE_CLASS); // TODO own style class

        final ChangeListener<String> changeListener = (observable, oldValue, newValue) -> this.hasChanged
                .set(true);
        valueTextField.textProperty().addListener(changeListener);

        vbox.getChildren().setAll(titleLabel, valueTextField);

        return vbox;
    }

    private <T> VBox createField(final ReadOnlyStringProperty<T> property, final T data) {
        val vbox = new VBox();

        val titleLabel = new Label();
        titleLabel.setText(I18n.get(property.getTitle()));
        titleLabel.getStyleClass().add(TITLE_STYLE_CLASS);

        val valueLabel = new Label();
        val value = property.getPropertyExtractor().apply(data).get();
        valueLabel.setText(value);
        valueLabel.getStyleClass().add(READONLY_VALUE_STYLE_CLASS);

        vbox.getChildren().setAll(titleLabel, valueLabel);

        return vbox;
    }

    @Override
    protected void bindLabels() {

    }

    @Override
    public BooleanProperty hasChanged() {
        return hasChanged;
    }

    @Override
    public void saveChanges() {

    }

    @Override
    public void dropChanges() {

    }
}
