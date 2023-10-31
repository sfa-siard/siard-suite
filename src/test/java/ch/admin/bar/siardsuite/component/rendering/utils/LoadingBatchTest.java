package ch.admin.bar.siardsuite.component.rendering.utils;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LoadingBatchTest {

    @Test
    public void calculateDistance_indexInOneOfThePreviousBatches_expectNegativeDistance() {
        // given
        val batch = LoadingBatch.createMatchingLoadingBatch(218);

        // when
        val batchDistance = batch.calculateDistance(LoadingBatch.createMatchingLoadingBatch(49));


        // then
        Assertions.assertThat(batchDistance).isEqualTo(-4);
    }

    @Test
    public void calculateDistance_indexInSameBatch_expectNoDistance() {
        // given
        val batch = LoadingBatch.createMatchingLoadingBatch(0);

        // when
        val batchDistance = batch.calculateDistance(LoadingBatch.createMatchingLoadingBatch(49));


        // then
        Assertions.assertThat(batchDistance).isEqualTo(0);
    }

    @Test
    public void calculateDistance_indexInOneOfTheNextBatches_expectPositiveDistance() {
        // given
        val batch = LoadingBatch.createMatchingLoadingBatch(0);

        // when
        val batchDistance = batch.calculateDistance(LoadingBatch.createMatchingLoadingBatch(249));


        // then
        Assertions.assertThat(batchDistance).isEqualTo(4);
    }

    @Test
    public void createMatchingBatch_withIndexInsideFirstBatch_expectFirstBatch() {
        // given

        // when
        val batch = LoadingBatch.createMatchingLoadingBatch(49);

        // then
        Assertions.assertThat(batch.getStartIndex()).isEqualTo(0);
        Assertions.assertThat(batch.getNrOfElements()).isEqualTo(50);
        Assertions.assertThat(batch.getBatchNr()).isEqualTo(0);
    }

    @Test
    public void createMatchingBatch_withIndexInsideThirdBatch_expectThirdBatch() {
        // given

        // when
        val batch = LoadingBatch.createMatchingLoadingBatch(123);

        // then
        Assertions.assertThat(batch.getStartIndex()).isEqualTo(100);
        Assertions.assertThat(batch.getNrOfElements()).isEqualTo(50);
        Assertions.assertThat(batch.getBatchNr()).isEqualTo(2);
    }

}