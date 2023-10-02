package ch.admin.bar.siardsuite.presenter.tree;

import javafx.beans.property.BooleanProperty;

public interface ChangeableDataPresenter {

    BooleanProperty hasChanged();
    void saveChanges();
    void dropChanges();
}
