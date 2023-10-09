package ch.admin.bar.siardsuite.component.rendering.utils;


import ch.admin.bar.siardsuite.component.rendering.model.LazyLoadingDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class LoadingBatchManager<T> {

    @Getter
    private final ObservableList<T> observableList = FXCollections.observableArrayList();
    private final Set<LoadingBatch> loadedBatches = new HashSet<>();

    private final LazyLoadingDataSource<T> dataSource;

    public void loadDataIfNecessary(final long index) {
        val matchingBatch = LoadingBatch.createMatchingLoadingBatch(index + 2);

        if (loadedBatches.contains(matchingBatch)) {
            // batch already loaded
            return;
        }

        log.info("Data for index {} is not yet available, loading batch {} with start index {} (and length {})",
                index,
                matchingBatch.getBatchNr(),
                matchingBatch.getStartIndex(),
                matchingBatch.getNrOfElements());

        loadedBatches.add(matchingBatch);

        val data = dataSource.load(
                (int) matchingBatch.getStartIndex(),
                (int) matchingBatch.getNrOfElements());

        observableList.addAll(data);
    }
}
