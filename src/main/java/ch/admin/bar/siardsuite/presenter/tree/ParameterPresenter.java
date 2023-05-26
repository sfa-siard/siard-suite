package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import static ch.admin.bar.siardsuite.util.I18n.bind;

public class ParameterPresenter extends TreePresenter {

    @FXML
    public VBox container;
    @FXML
    public VBox labels;
    @FXML
    public VBox texts;
    @FXML
    public Label name;
    @FXML
    public Label position;
    @FXML
    public Label mode;
    @FXML
    public Label sqlType;
    @FXML
    public Label udtSchema;
    @FXML
    public Label udtName;
    @FXML
    public Label originalDataType;
    @FXML
    public Label arrayParameterCardinality;
    @FXML
    public Label description;

    @Override
    public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        bindLabels();

        model.populate(texts, wrapper.getDatabaseObject(), wrapper.getType());
    }

    private void bindLabels() {
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
