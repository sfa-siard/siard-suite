package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.component.SiardLabelContainer;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.facades.MetaParameterFacade;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

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
    public String name() {
        return metaParameter.getName();
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {

    }

    @Override
    public void populate(TableView<Map> tableView, TreeContentView type) {

    }

    @Override
    public void populate(VBox container, TreeContentView type) {
        new SiardLabelContainer(container).withLabel(metaParameter.getName(), NAME)
                                          .withLabel(String.valueOf(metaParameter.getPosition()), POSITION)
                                          .withLabel(metaParameter.getMode(), MODE)
                                          .withLabel(metaParameter.getType(), SQL_TYPE)
                                          .withLabel(metaParameter.getTypeSchema(), UDT_SCHEMA)
                                          .withLabel(metaParameter.getTypeName(), TYPE_NAME)
                                          .withLabel(metaParameter.getTypeOriginal(), ORIGINAL_DATA_TYPE)
                                          .withLabel(String.valueOf(metaParameterFacade.formattedCardinality()),
                                                     CARDINALITY)
                                          .withLabel(metaParameter.getDescription(), DESCRIPTION);

        for (Node node : container.getChildren()) {
            node.getStyleClass().add("table-container-label-small");
        }
    }

    private static final String NAME = "name";
    private static final String POSITION = "position";
    private static final String MODE = "mode";
    private static final String SQL_TYPE = "sqlType";
    private static final String UDT_SCHEMA = "udtSchema";
    private static final String TYPE_NAME = "udtName";
    private static final String ORIGINAL_DATA_TYPE = "originalDataType";
    private static final String CARDINALITY = "cardinality";
    private static final String DESCRIPTION = "description";

}
