package ch.admin.bar.siardsuite.ui.component.rendering.utils;

import ch.admin.bar.siardsuite.ui.component.rendering.model.LazyLoadingDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
public class LoadingBatchManager<T> {

    private static final int LOADING_DISTANCE = 50;

    private final List<T> completeList = new ArrayList<>();
    
    @Getter
    private final ObservableList<T> observableList = FXCollections.observableArrayList();
    
    private final Set<LoadingBatch> loadedBatches = new HashSet<>();
    private final LazyLoadingDataSource<T> dataSource;
    
    private Predicate<T> currentFilter = item -> true;

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

        completeList.addAll(data);
        
        for (T item : data) {
            if (currentFilter.test(item)) {
                observableList.add(item);
            }
        }
    }

    public void applyFilter(Predicate<T> filter) {
        this.currentFilter = filter;
        
        observableList.clear();
        for (T item : completeList) {
            if (filter.test(item)) {
                observableList.add(item);
            }
        }
    }

    public void clearFilter() {
        this.currentFilter = item -> true;
        
        observableList.clear();
        observableList.addAll(completeList);
    }

    public boolean loadedAll() {
        val currentlyLoaded = loadedBatches.stream()
                .mapToLong(LoadingBatch::getNrOfElements)
                .sum();

        return currentlyLoaded >= dataSource.getNumberOfItems();
    }

    public long getLastLoadingIndex() {
        val latestBatch = loadedBatches.stream()
                .reduce((loadingBatch, loadingBatch2) -> {
                    if (loadingBatch.getBatchNr() > loadingBatch2.getBatchNr()) {
                        return loadingBatch;
                    }
                    return loadingBatch2;
                });

        return latestBatch
                .map(batch -> batch.getStartIndex() + batch.getNrOfElements() - LOADING_DISTANCE)
                .orElse(0L);
    }
}
