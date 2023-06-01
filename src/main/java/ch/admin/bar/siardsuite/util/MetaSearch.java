package ch.admin.bar.siardsuite.util;

import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.DatabaseObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class MetaSearch {

    private final MetaSearchTerm metaSearchTerm;
    final List<String> nodeIds = new ArrayList<>();

    public MetaSearch(MetaSearchTerm metaSearchTerm) {
        this.metaSearchTerm = metaSearchTerm;
    }

    public MetaSearch check(String value, String nodeId) {
        if (metaSearchTerm.matches(value)) nodeIds.add(nodeId);
        return this;
    }

    public TreeSet<MetaSearchHit> hits(String displayName, DatabaseObject databaseObject,
                                       TreeContentView treeContentView) {

        if (nodeIds.isEmpty()) return new TreeSet<>(Collections.emptyList());
        return new TreeSet<>(Collections.singletonList(new MetaSearchHit(displayName,
                                                                         databaseObject,
                                                                         treeContentView,
                                                                         nodeIds)));
    }
}
