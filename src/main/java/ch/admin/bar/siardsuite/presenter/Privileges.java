package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import ch.admin.bar.siardsuite.model.database.Privilige;
import ch.admin.bar.siardsuite.model.database.PriviligeVisitor;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Privileges extends DatabaseObject {

    private final List<Privilige> privileges;

    public Privileges(List<Privilige> privileges) {
        this.privileges = privileges;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {
    }
}
