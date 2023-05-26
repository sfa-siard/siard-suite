package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import javafx.scene.control.TableView;

import java.util.Map;

public interface TableViewPopulatorStrategy<T extends DatabaseObject> {


    void populate(TableView<Map> tableView, boolean onlyMetaData);

}
