package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import javafx.scene.Node;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.assertions.api.Assertions;

import java.util.function.Consumer;

class StepsChainBuilderTest {

    private static final DummyView<Void, SecondStepData> FIRST_VIEW = new DummyView<>();
    private static final DummyView<SecondStepData, ThirdStepData> SECOND_VIEW = new DummyView<>();
    private static final DummyView<ThirdStepData, FourthStepData> THIRD_VIEW = new DummyView<>();
    private static final DummyView<FourthStepData, Void> FOURTH_VIEW = new DummyView<>();

    private final Consumer<LoadedFxml> displayServiceMock = Mockito.mock(Consumer.class);
    private final ServicesFacade servicesFacadeMock = Mockito.mock(ServicesFacade.class);

    @Test
    public void test() {
        // given
        new StepsChainBuilder(displayServiceMock, servicesFacadeMock)
                .register(StepDefinition.<Void, SecondStepData>builder()
                        .inputType(Void.class)
                        .outputType(SecondStepData.class)
                        .viewLoader(FIRST_VIEW::load)
                        .title(DisplayableText.of("First"))
                        .build())
                .register(StepDefinition.<SecondStepData, ThirdStepData>builder()
                        .inputType(SecondStepData.class)
                        .outputType(ThirdStepData.class)
                        .viewLoader(SECOND_VIEW::load)
                        .title(DisplayableText.of("Second"))
                        .build())
                .register(StepDefinition.<ThirdStepData, FourthStepData>builder()
                        .inputType(ThirdStepData.class)
                        .outputType(FourthStepData.class)
                        .viewLoader(THIRD_VIEW::load)
                        .title(DisplayableText.of("Third"))
                        .build())
                .register(StepDefinition.<FourthStepData, Void>builder()
                        .inputType(FourthStepData.class)
                        .outputType(Void.class)
                        .viewLoader(FOURTH_VIEW::load)
                        .title(DisplayableText.of("Fourth"))
                        .build())
                .build();

        // when

        // then
        Assertions.assertThat(FIRST_VIEW.loadedCounter).isEqualTo(1);
        Assertions.assertThat(SECOND_VIEW.loadedCounter).isEqualTo(0);
        Assertions.assertThat(THIRD_VIEW.loadedCounter).isEqualTo(0);
        Assertions.assertThat(FOURTH_VIEW.loadedCounter).isEqualTo(0);
    }

    @Test
    public void test2() {
        // given
        new StepsChainBuilder(displayServiceMock, servicesFacadeMock)
                .register(StepDefinition.<Void, SecondStepData>builder()
                        .inputType(Void.class)
                        .outputType(SecondStepData.class)
                        .viewLoader(FIRST_VIEW::load)
                        .build())
                .register(StepDefinition.<SecondStepData, ThirdStepData>builder()
                        .inputType(SecondStepData.class)
                        .outputType(ThirdStepData.class)
                        .viewLoader(SECOND_VIEW::load)
                        .build())
                .register(StepDefinition.<ThirdStepData, FourthStepData>builder()
                        .inputType(ThirdStepData.class)
                        .outputType(FourthStepData.class)
                        .viewLoader(THIRD_VIEW::load)
                        .build())
                .register(StepDefinition.<FourthStepData, Void>builder()
                        .inputType(FourthStepData.class)
                        .outputType(Void.class)
                        .viewLoader(FOURTH_VIEW::load)
                        .build())
                .build();

        // when
        FIRST_VIEW.getNavigator().next(new SecondStepData());
        SECOND_VIEW.getNavigator().next(new ThirdStepData());
        THIRD_VIEW.getNavigator().next(new FourthStepData());
        FOURTH_VIEW.getNavigator().previous();

        // then
        Assertions.assertThat(FIRST_VIEW.loadedCounter).isEqualTo(1);
        Assertions.assertThat(SECOND_VIEW.loadedCounter).isEqualTo(1);
        Assertions.assertThat(THIRD_VIEW.loadedCounter).isEqualTo(2);
        Assertions.assertThat(FOURTH_VIEW.loadedCounter).isEqualTo(1);
    }

    @Getter
    private static class DummyView<TIn, TOut> {
        private StepperNavigator<TOut> navigator = null;
        private TIn data = null;
        private int loadedCounter = 0;

        public LoadedFxml load(final TIn data, final StepperNavigator<TOut> navigator) {
            this.navigator = navigator;
            this.data = data;
            loadedCounter++;

            return new LoadedFxml(() -> Mockito.mock(Node.class), new Object());
        }
    }

    public static class SecondStepData {

    }

    public static class ThirdStepData {

    }

    public static class FourthStepData {

    }

}