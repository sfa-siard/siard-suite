package ch.admin.bar.siardsuite.ui.component.rendering.utils;

import lombok.Value;
import lombok.val;

@Value
public class LoadingBatch {
    public static final int BATCH_SIZE = 100;

    long startIndex;
    long nrOfElements;
    long batchNr;

    public static LoadingBatch createMatchingLoadingBatch(long index) {
        val batchIndex = index % BATCH_SIZE;
        val startIndex = (index - batchIndex);
        val batchNr = startIndex / BATCH_SIZE;

        return new LoadingBatch(startIndex, BATCH_SIZE, batchNr);
    }
}
