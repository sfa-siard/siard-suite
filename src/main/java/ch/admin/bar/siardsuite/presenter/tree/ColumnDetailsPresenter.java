package ch.admin.bar.siardsuite.presenter.tree;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static ch.admin.bar.siardsuite.util.I18n.bind;

public class ColumnDetailsPresenter extends DetailsPresenter {

    @FXML
    private Label name;
    @FXML
    private Label position;
    @FXML
    private Label lob;
    @FXML
    private Label mime;
    @FXML
    private Label sql;
    @FXML
    private Label udtSchema;
    @FXML
    private Label udtName;
    @FXML
    private Label dataType;
    @FXML
    private Label nullable;
    @FXML
    private Label defaultValue;
    @FXML
    private Label cardinality;
    @FXML
    private Label description;

    @Override
    protected void bindLabels() {
        bind(name, "columnDetails.name");
        bind(position, "columnDetails.position");
        bind(lob, "columnDetails.lob");
        bind(mime, "columnDetails.mime");
        bind(sql, "columnDetails.sql");
        bind(udtSchema, "columnDetails.udtSchema");
        bind(udtName, "columnDetails.udtName");
        bind(dataType, "columnDetails.dataType");
        bind(nullable, "columnDetails.nullable");
        bind(defaultValue, "columnDetails.defaultValue");
        bind(cardinality, "columnDetails.cardinality");
        bind(description, "columnDetails.description");

    }
}
