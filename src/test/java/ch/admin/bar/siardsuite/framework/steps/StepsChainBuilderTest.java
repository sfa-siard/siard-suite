package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.scene.Node;
import lombok.Getter;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.assertions.api.Assertions;

import java.util.ArrayList;
import java.util.List;


class StepsChainBuilderTest {

    private static final DummyView<Void, SecondStepData> FIRST_VIEW = new DummyView<>();
    private static final DummyView<SecondStepData, ThirdStepData> SECOND_VIEW = new DummyView<>();
    private static final DummyView<ThirdStepData, FourthStepData> THIRD_VIEW = new DummyView<>();
    private static final DummyView<FourthStepData, Void> FOURTH_VIEW = new DummyView<>();

    private static final StepDefinition<Void, SecondStepData> FIRST_STEP = new StepDefinition<>(I18nKey.of("FIRST"), FIRST_VIEW::load);
    private static final StepDefinition<SecondStepData, ThirdStepData> SECOND_STEP = new StepDefinition<>(I18nKey.of("SECOND"), SECOND_VIEW::load);
    private static final StepDefinition<ThirdStepData, FourthStepData> THIRD_STEP = new StepDefinition<>(I18nKey.of("THIRD"), THIRD_VIEW::load);
    private static final StepDefinition<FourthStepData, Void> FOURTH_STEP = new StepDefinition<>(I18nKey.of("FOURTH"), FOURTH_VIEW::load);


    private final ServicesFacade servicesFacadeMock = Mockito.mock(ServicesFacade.class);

    @BeforeEach
    public void setup() {
        FIRST_VIEW.reset();
        SECOND_VIEW.reset();
        THIRD_VIEW.reset();
        FOURTH_VIEW.reset();
    }

    @Test
    public void navigateTroughChain_forwardOnlyWithStepsOnly_expectValidInjectedData() {
        // given
        val chain = new StepsChainBuilder(
                servicesFacadeMock,
                step -> step.getViewSupplier().get(),
                step -> step.getViewSupplier().get()
        )
                .register(FIRST_STEP)
                .register(SECOND_STEP)
                .register(THIRD_STEP)
                .register(FOURTH_STEP)
                .build();

        val secondStepData = new SecondStepData();
        val thirdStepData = new ThirdStepData();
        val fourthStepData = new FourthStepData();

        // when
        chain.getNavigatorOfStep(FIRST_STEP).next(secondStepData);
        chain.getNavigatorOfStep(SECOND_STEP).next(thirdStepData);
        chain.getNavigatorOfStep(THIRD_STEP).next(fourthStepData);

        // then
        Assertions.assertThat(SECOND_VIEW.injectedData).containsExactly(secondStepData);
        Assertions.assertThat(THIRD_VIEW.injectedData).containsExactly(thirdStepData);
        Assertions.assertThat(FOURTH_VIEW.injectedData).containsExactly(fourthStepData);
    }

    @Test
    public void navigateTroughChain_forwardAndBackwardWithStepsOnly_expectValidInjectedData() {
        // given
        val chain = new StepsChainBuilder(
                servicesFacadeMock,
                step -> step.getViewSupplier().get(),
                step -> step.getViewSupplier().get()
        )
                .register(FIRST_STEP)
                .register(SECOND_STEP)
                .register(THIRD_STEP)
                .register(FOURTH_STEP)
                .build();

        val secondStepData = new SecondStepData();
        val thirdStepData = new ThirdStepData();
        val fourthStepData = new FourthStepData();

        // when
        chain.getNavigatorOfStep(FIRST_STEP).next(secondStepData);
        chain.getNavigatorOfStep(SECOND_STEP).next(thirdStepData);
        chain.getNavigatorOfStep(THIRD_STEP).next(fourthStepData);
        chain.getNavigatorOfStep(FOURTH_STEP).previous();
        chain.getNavigatorOfStep(THIRD_STEP).previous();
        chain.getNavigatorOfStep(SECOND_STEP).previous();

        // then
        Assertions.assertThat(FIRST_VIEW.injectedData).hasSize(1);
        Assertions.assertThat(SECOND_VIEW.injectedData).containsExactly(secondStepData, secondStepData);
        Assertions.assertThat(THIRD_VIEW.injectedData).containsExactly(thirdStepData, thirdStepData);
        Assertions.assertThat(FOURTH_VIEW.injectedData).containsExactly(fourthStepData);
    }

    @Test
    public void navigateTroughChain_forwardOnlyWithTransformer_expectValidInjectedData() {
        // given
        val secondStepData = new SecondStepData();
        val thirdStepData = new ThirdStepData();
        val fourthStepData = new FourthStepData();

        val chain = new StepsChainBuilder(
                servicesFacadeMock,
                step -> step.getViewSupplier().get(),
                step -> step.getViewSupplier().get()
        )
                .register(FIRST_STEP)
                .transform(data -> {
                    Assertions.assertThat(data).isEqualTo(secondStepData);
                    return thirdStepData;
                })
                .transform(data -> {
                    Assertions.assertThat(data).isEqualTo(thirdStepData);
                    return fourthStepData;
                })
                .register(FOURTH_STEP)
                .build();

        // when
        chain.getNavigatorOfStep(FIRST_STEP).next(secondStepData);

        // then
        Assertions.assertThatThrownBy(() -> chain.getNavigatorOfStep(SECOND_STEP).next(thirdStepData))
                        .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> chain.getNavigatorOfStep(THIRD_STEP).next(fourthStepData))
                        .isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThat(SECOND_VIEW.injectedData).isEmpty();
        Assertions.assertThat(THIRD_VIEW.injectedData).isEmpty();
        Assertions.assertThat(FOURTH_VIEW.injectedData).containsExactly(fourthStepData);
    }

    @Test
    public void navigateTroughChain_forwardAndBackwardWithTransformer_expectValidInjectedData() {
        // given
        val secondStepData = new SecondStepData();
        val thirdStepData = new ThirdStepData();
        val fourthStepData = new FourthStepData();

        val chain = new StepsChainBuilder(
                servicesFacadeMock,
                step -> step.getViewSupplier().get(),
                step -> step.getViewSupplier().get()
        )
                .register(FIRST_STEP)
                .transform(data -> {
                    Assertions.assertThat(data).isEqualTo(secondStepData);
                    return thirdStepData;
                })
                .transform(data -> {
                    Assertions.assertThat(data).isEqualTo(thirdStepData);
                    return fourthStepData;
                })
                .register(FOURTH_STEP)
                .build();

        // when
        chain.getNavigatorOfStep(FIRST_STEP).next(secondStepData);
        chain.getNavigatorOfStep(FOURTH_STEP).previous();

        // then
        Assertions.assertThatThrownBy(() -> chain.getNavigatorOfStep(SECOND_STEP).next(thirdStepData))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> chain.getNavigatorOfStep(THIRD_STEP).next(fourthStepData))
                .isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThat(FIRST_VIEW.injectedData).hasSize(1);
        Assertions.assertThat(FIRST_VIEW.injectedData.get(0)).isNull();
        Assertions.assertThat(SECOND_VIEW.injectedData).isEmpty();
        Assertions.assertThat(THIRD_VIEW.injectedData).isEmpty();
        Assertions.assertThat(FOURTH_VIEW.injectedData).containsExactly(fourthStepData);
    }

    @Getter
    private static class DummyView<TIn, TOut> {
        private StepperNavigator<TOut> navigator = null;
        private final List<TIn> injectedData = new ArrayList<>();

        public LoadedFxml load(final TIn data, final StepperNavigator<TOut> navigator) {
            this.navigator = navigator;
            this.injectedData.add(data);

            return new LoadedFxml(() -> Mockito.mock(Node.class), new Object());
        }

        public void reset() {
            navigator = null;
            injectedData.clear();
        }
    }

    public static class SecondStepData {

    }

    public static class ThirdStepData {

    }

    public static class FourthStepData {

    }

}