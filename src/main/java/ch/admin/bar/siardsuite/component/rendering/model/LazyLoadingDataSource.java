package ch.admin.bar.siardsuite.component.rendering.model;

import java.io.IOException;
import java.util.List;

public interface LazyLoadingDataSource<T> {
    long getNrOfEntries() throws IOException;

    List<T> load(int startIndex, int nrOfItems);

    long findIndexOf(T item);
}
