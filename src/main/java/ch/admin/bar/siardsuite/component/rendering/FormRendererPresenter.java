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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FormRendererPresenter extends DetailsPresenter implements ChangeableDataPresenter {

    @FXML
    private VBox container;

    private FormRenderer<?> formRenderer;

    @Override
    public void init(Controller controller, RootStage stage, TreeAttributeWrapper wrapper) {

        this.formRenderer = new FormRenderer<>(wrapper.getRenderableForm()
                .orElseThrow(() -> new IllegalArgumentException("No renderable form provided")),
                controller);

        this.container.getChildren().addAll(formRenderer.renderForm());
    }

    @Override
    protected void bindLabels() {
        // TODO re-render table (switch of language)
    }

    @Override
    public BooleanProperty hasChanged() {
        return formRenderer.getHasChanged();
    }

    @Override
    public void saveChanges() {
        formRenderer.saveChanges();
    }

    @Override
    public void dropChanges() {
        formRenderer.dropChanges();
    }
}
