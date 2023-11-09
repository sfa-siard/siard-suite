package ch.admin.bar.siardsuite.component.rendering.utils;


import ch.admin.bar.siardsuite.component.rendering.model.LazyLoadingDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class LoadingBatchManager<T> {

    private static final int LOADING_DISTANCE = 50;

    @Getter
    private final ObservableList<T> observableList = FXCollections.observableArrayList();
    private final Set<LoadingBatch> loadedBatches = new HashSet<>();

    private final LazyLoadingDataSource<T> dataSource;

    public LoadingBatchManager(LazyLoadingDataSource<T> dataSource) {
        this.dataSource = dataSource;
        this.loadDataIfNecessary(0);
    }

    public void loadDataIfNecessary(final long index) {
        val thresholdIndex = index + LOADING_DISTANCE;
        val matchingBatch = LoadingBatch.createMatchingLoadingBatch(thresholdIndex);

        if (loadedBatches.contains(matchingBatch)) {
            // batch already loaded
            return;
        }

        log.info("Data for threshold-index {} is not yet available, loading batch {} with start index {} (and length {})",
                 thresholdIndex,
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
