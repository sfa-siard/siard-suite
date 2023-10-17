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
}
