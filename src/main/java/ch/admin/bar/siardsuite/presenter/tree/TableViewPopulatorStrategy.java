package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Map;

public interface TableViewPopulatorStrategy {


    void populate(TableView<Map> tableView, List<DatabaseTable> tables, boolean onlyMetaData);

}
