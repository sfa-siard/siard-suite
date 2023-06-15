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

    @Override
    public void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView != null && type != null) {
            final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col1 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col2 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col3 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col4 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col5 = new TableColumn<>();
            col0.textProperty().bind(I18n.createStringBinding("tableContainer.priviliges.header.type"));
            col1.textProperty().bind(I18n.createStringBinding("tableContainer.priviliges.header.object"));
            col2.textProperty().bind(I18n.createStringBinding("tableContainer.priviliges.header.grantor"));
            col3.textProperty().bind(I18n.createStringBinding("tableContainer.priviliges.header.receiver"));
            col4.textProperty().bind(I18n.createStringBinding("tableContainer.priviliges.header.option"));
            col5.textProperty().bind(I18n.createStringBinding("tableContainer.priviliges.header.description"));
            col0.setCellValueFactory(new MapValueFactory<>("type"));
            col1.setCellValueFactory(new MapValueFactory<>("object"));
            col2.setCellValueFactory(new MapValueFactory<>("grantor"));
            col3.setCellValueFactory(new MapValueFactory<>("receiver"));
            col4.setCellValueFactory(new MapValueFactory<>("option"));
            col5.setCellValueFactory(new MapValueFactory<>("description"));
            tableView.getColumns().add(col0);
            tableView.getColumns().add(col1);
            tableView.getColumns().add(col2);
            tableView.getColumns().add(col3);
            tableView.getColumns().add(col4);
            tableView.getColumns().add(col5);
            tableView.setItems(items());
        }
    }

    private ObservableList<Map> items() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        PriviligeToMapVisitor visitor = new PriviligeToMapVisitor();
        for (Privilige privilige : privileges) {
            items.add(privilige.accept(visitor));
        }
        return items;
    }

    @Override
    public void populate(VBox vBox, TreeContentView type) {

    }

    private class PriviligeToMapVisitor implements PriviligeVisitor<Map<String, String>> {

        @Override
        public Map<String, String> visit(String type, String object, String grantor, String grantee, String option,
                                         String description) {
            Map<String, String> item = new HashMap<>();
            item.put("type", type);
            item.put("object", object);
            item.put("grantor", grantor);
            item.put("receiver", grantee);
            item.put("option", option);
            item.put("description", description);
            return item;
        }
    }
}
