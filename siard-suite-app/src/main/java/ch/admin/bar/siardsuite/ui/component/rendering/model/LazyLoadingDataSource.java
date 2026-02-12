package ch.admin.bar.siardsuite.ui.component.rendering.model;

import java.util.List;

public interface LazyLoadingDataSource<T> {
    List<T> load(int startIndex, int nrOfItems);

    long findIndexOf(T item);

    long getNumberOfItems();
}
