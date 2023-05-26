package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.facades.MetaParameterFacade;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseParameter extends DatabaseObject {

    private MetaParameter metaParameter;
    private MetaParameterFacade metaParameterFacade;

    public DatabaseParameter(MetaParameter metaParameter) {
        super();
        this.metaParameter = metaParameter;
        this.metaParameterFacade = new MetaParameterFacade(metaParameter);
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {

    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {

    }

    @Override
    protected void populate(VBox vBox, TreeContentView type) {
        List<Label> labels = new ArrayList<>();
        labels.add(createLabel(metaParameter.getName(), "name"));
        labels.add(createLabel(String.valueOf(metaParameter.getPosition()), "position"));
        labels.add(createLabel(metaParameter.getMode(), "mode"));
        labels.add(createLabel(metaParameter.getTypeName(), "sqlType"));
        labels.add(createLabel(metaParameter.getTypeSchema(), "udtSchema"));
        labels.add(createLabel(metaParameter.getTypeName(), "udtName"));
        labels.add(createLabel(metaParameter.getTypeOriginal(), "originalDataType"));
        labels.add(createLabel(String.valueOf(metaParameterFacade.formattedCardinality()), "cardinality"));
        labels.add(createLabel(metaParameter.getDescription(), "description"));

        vBox.getChildren().addAll(labels);
        for (Node node : vBox.getChildren()) {
            node.getStyleClass().add("table-container-label-small");
        }
    }

    private static Label createLabel(String value, String id) {
        Label label = new Label(value != null && !value.isEmpty() ? value : "-");
        label.setId(id);
        return label;
    }
}
