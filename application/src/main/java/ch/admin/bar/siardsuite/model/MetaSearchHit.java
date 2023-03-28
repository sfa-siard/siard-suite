package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.model.database.DatabaseObject;

import java.util.List;
import java.util.Objects;

public final class MetaSearchHit implements Comparable<MetaSearchHit> {
    private final String displayName;
    private final DatabaseObject databaseObject;
    private final TreeContentView treeContentView;
    private final List<String> nodeIds;

    public MetaSearchHit(String displayName, DatabaseObject databaseObject, TreeContentView treeContentView,
                         List<String> nodeIds) {
        this.displayName = displayName;
        this.databaseObject = databaseObject;
        this.treeContentView = treeContentView;
        this.nodeIds = nodeIds;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaSearchHit that = (MetaSearchHit) o;
        return displayName.equals(that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName);
    }

    @Override
    public int compareTo(MetaSearchHit o) {
        return displayName.compareTo(o.displayName);
    }

    public String displayName() {
        return displayName;
    }

    public DatabaseObject databaseObject() {
        return databaseObject;
    }

    public TreeContentView treeContentView() {
        return treeContentView;
    }

    public List<String> nodeIds() {
        return nodeIds;
    }

}
