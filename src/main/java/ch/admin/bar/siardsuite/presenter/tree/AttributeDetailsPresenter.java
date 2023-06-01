package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.util.I18n;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AttributeDetailsPresenter extends DetailsPresenter {
    @FXML
    public Label name;
    @FXML
    public Label position;
    @FXML
    public Label sqlType;
    @FXML
    public Label udtSchema;
    @FXML
    public Label udtName;
    @FXML
    public Label originalDataType;
    @FXML
    public Label allowsNull;
    @FXML
    public Label defaultValue;
    @FXML
    public Label arrayCardinality;
    @FXML
    public Label description;

    @Override
    protected void bindLabels() {
        I18n.bind(name, "attribute.name");
        I18n.bind(position, "attribute.position");
        I18n.bind(sqlType, "attribute.sqlType");
        I18n.bind(udtSchema, "attribute.udtSchema");
        I18n.bind(udtName, "attribute.udtName");
        I18n.bind(originalDataType, "attribute.originalDataType");
        I18n.bind(allowsNull, "attribute.allowsNull");
        I18n.bind(defaultValue, "attribute.defaultValue");
        I18n.bind(arrayCardinality, "attribute.arrayCardinality");
        I18n.bind(description, "attribute.description");
    }
}
