package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.presenter.archive.browser.ChangeableDataPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.SearchableTableContainer;
import ch.admin.bar.siardsuite.presenter.tree.DetailsPresenter;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class FormRendererPresenter extends DetailsPresenter implements ChangeableDataPresenter, SearchableTableContainer {

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
    public SaveChangesReport saveChanges() {
        return formRenderer.saveChanges();
    }

    @Override
    public void dropChanges() {
        formRenderer.dropChanges();
    }

    @Override
    public BooleanProperty hasSearchableData() {
        return formRenderer.getHasSearchableData();
    }

    @Override
    public void applySearchTerm(String searchTerm) {
        formRenderer.applySearchTerm(searchTerm);
    }

    @Override
    public void clearSearchTerm() {
        formRenderer.clearSearchTerm();
    }
}
