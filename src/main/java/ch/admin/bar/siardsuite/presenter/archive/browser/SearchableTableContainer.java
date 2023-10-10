package ch.admin.bar.siardsuite.presenter.archive.browser;

import javafx.beans.property.BooleanProperty;

public interface SearchableTableContainer {
    BooleanProperty hasSearchableData();

    void applySearchTerm(String searchTerm);

    void clearSearchTerm();
}
