package ch.admin.bar.siardsuite.component.rendering.utils;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LoadingBatchTest {
    @Test
    public void createMatchingBatch_withIndexInsideFirstBatch_expectFirstBatch() {
        // given

        // when
        val batch = LoadingBatch.createMatchingLoadingBatch(LoadingBatch.BATCH_SIZE - 1);

        // then
        Assertions.assertThat(batch.getStartIndex()).isEqualTo(0);
        Assertions.assertThat(batch.getNrOfElements()).isEqualTo(LoadingBatch.BATCH_SIZE);
        Assertions.assertThat(batch.getBatchNr()).isEqualTo(0);
    }

    @Test
    public void createMatchingBatch_withIndexInsideThirdBatch_expectThirdBatch() {
        // given

        // when
        val batch = LoadingBatch.createMatchingLoadingBatch(LoadingBatch.BATCH_SIZE + 23);

        // then
        Assertions.assertThat(batch.getStartIndex()).isEqualTo(LoadingBatch.BATCH_SIZE);
        Assertions.assertThat(batch.getNrOfElements()).isEqualTo(LoadingBatch.BATCH_SIZE);
        Assertions.assertThat(batch.getBatchNr()).isEqualTo(1);
    }

}