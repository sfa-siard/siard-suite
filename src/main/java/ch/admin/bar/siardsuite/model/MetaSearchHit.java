package ch.admin.bar.siardsuite.model;

import java.util.List;
import java.util.Objects;

public record MetaSearchHit(String displayName, TreeContentView treeContentView, List<String> nodeIds) {

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
}
