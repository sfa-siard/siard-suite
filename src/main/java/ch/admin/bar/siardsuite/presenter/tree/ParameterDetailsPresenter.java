package ch.admin.bar.siardsuite.presenter.tree;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static ch.admin.bar.siardsuite.util.I18n.bind;

public class ParameterDetailsPresenter extends DetailsPresenter {

    @FXML
    private Label name;
    @FXML
    private Label position;
    @FXML
    private Label mode;
    @FXML
    private Label sqlType;
    @FXML
    private Label udtSchema;
    @FXML
    private Label udtName;
    @FXML
    private Label originalDataType;
    @FXML
    private Label arrayParameterCardinality;
    @FXML
    private Label description;

    protected void bindLabels() {
        bind(name, "parameter.name");
        bind(position, "parameter.position");
        bind(mode, "parameter.mode");
        bind(sqlType, "parameter.sqlType");
        bind(udtSchema, "parameter.udtSchema");
        bind(udtName, "parameter.udtName");
        bind(originalDataType, "parameter.originalDataType");
        bind(arrayParameterCardinality, "parameter.arrayParameterCardinality");
        bind(description, "parameter.description");
    }
}
