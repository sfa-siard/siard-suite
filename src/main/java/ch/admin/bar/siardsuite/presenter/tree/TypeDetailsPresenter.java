package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class TypeDetailsPresenter extends DetailsPresenter {

    @FXML
    public Label nameLabel;
    @FXML
    public Label typeCategoryLabel;
    @FXML
    public Label isInstantiableLabel;
    @FXML
    public Label isFinalLabel;
    @FXML
    public Label baseTypeLabel;
    @FXML
    public Label descriptionLabel;
    @FXML
    public TableView tableView;

    @Override
    public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) {
        super.init(controller, model, stage, wrapper);
        model.populate(tableView, wrapper.getDatabaseObject(), wrapper.getType());
    }

    @Override
    protected void bindLabels() {
        I18n.bind(nameLabel, "type.name.label");
        I18n.bind(typeCategoryLabel, "type.category.label");
        I18n.bind(isInstantiableLabel, "type.isInstantiable.label");
        I18n.bind(isFinalLabel, "type.isFinal.label");
        I18n.bind(baseTypeLabel, "type.base.category.label");
        I18n.bind(descriptionLabel, "type.description.label");
    }
}
