package ch.admin.bar.siardsuite.model;

import javafx.scene.control.TableView;
import java.util.LinkedHashSet;
import java.util.Map;

public record TableSearchBase(TableView<Map> tableView, LinkedHashSet<Map> rows) { }
