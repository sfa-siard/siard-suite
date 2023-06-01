package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class DatabaseType extends DatabaseObject {
    private final String name;
    private final String category;
    private final boolean instantiable;
    private final boolean isFinal;
    private final String base;
    private final String description;


    private final List<DatabaseAttribute> databaseAttributes;

    public DatabaseType(String name, String category, boolean instantiable, boolean isFinal, String base,
                        String description, List<DatabaseAttribute> metaAttributes) {
        this.name = name;
        this.category = category;
        this.instantiable = instantiable;
        this.isFinal = isFinal;
        this.base = base;
        this.description = description;
        this.databaseAttributes = metaAttributes;
    }

    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visit(name, category, instantiable, isFinal, base, description);
    }

    public String name() {
        return name;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {

    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {

    }

    @Override
    protected void populate(VBox vBox, TreeContentView type) {

    }

    public int numberOfAttributes() {
        return this.databaseAttributes.size();
    }

    public List<DatabaseAttribute> attributes() {
        return databaseAttributes;
    }
}
