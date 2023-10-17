package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.util.I18n;
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
    protected void bindLabels() {
        I18n.bind(nameLabel, "type.name.label");
        I18n.bind(typeCategoryLabel, "type.category.label");
        I18n.bind(isInstantiableLabel, "type.isInstantiable.label");
        I18n.bind(isFinalLabel, "type.isFinal.label");
        I18n.bind(baseTypeLabel, "type.base.category.label");
        I18n.bind(descriptionLabel, "type.description.label");
    }
}
