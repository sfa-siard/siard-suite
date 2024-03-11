package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class StepsChainBuilder {

    private final ServicesFacade servicesFacade;

    private final Consumer<Step> onNextListener;
    private final Consumer<Step> onPreviousListener;

    /**
     * Registers a step without context in the steps chain.
     *
     * @param step The step to be registered.
     * @return The current builder instance for method chaining.
     */
    public <TPrevious> StepRegisterer<TPrevious> register(StepDefinition<Void, TPrevious> step) {
        val registerer = new StepRegisterer<Void>(servicesFacade, onNextListener, onPreviousListener);

        return registerer.register(step);
    }

    @RequiredArgsConstructor
    public static class StepRegisterer<TOutPrevious> {

        private final ServicesFacade servicesFacade;
        private final Consumer<Step> onNextListener;
        private final Consumer<Step> onPreviousListener;

        private final AtomicInteger indexNextStep = new AtomicInteger(0);
        private final Map<Integer, Object> cachedInputDataByStepIndex = new HashMap<>();
        private final List<Step> preparedSteps = new ArrayList<>();

        public <TOut> StepRegisterer<TOut> register(StepDefinition<TOutPrevious, TOut> step) {
            val stepIndex = indexNextStep.getAndIncrement();
            val navigator = createNavigator(stepIndex);

            final Supplier<LoadedFxml> viewLoader = () -> {
                val data = cachedInputDataByStepIndex.get(stepIndex);

                return step.getViewLoader()
                        .load((TOutPrevious) data, navigator, servicesFacade);
            };

            preparedSteps.add(Step.builder()
                    .definition(step)
                    .title(step.getTitle())
                    .navigator(navigator)
                    .stepIndex(stepIndex)
                    .viewSupplier(viewLoader)
                    .build());

            return (StepRegisterer<TOut>) this;
        }

        public <TOut> StepRegisterer<TOut> transform(Transformer<TOutPrevious, TOut> transformer) {
            val indexLastRegisteredStep = indexNextStep.get() - 1;
            val lastRegisteredStep = preparedSteps.get(indexLastRegisteredStep);

            preparedSteps.set(indexLastRegisteredStep, lastRegisteredStep.toBuilder()
                    .followingTransformer(transformer)
                    .build());

            return (StepRegisterer<TOut>) this;
        }

        /**
         * Builds the step chain based on the registered steps.
         *
         * @return The constructed {@link StepChain}.
         * @throws IllegalStateException If the steps chain is not valid.
         */
        public StepChain build() {
            return new StepChain(Collections.unmodifiableList(preparedSteps));
        }

        private StepperNavigator createNavigator(final int stepIndex) {
            return new StepperNavigator() {
                @Override
                public void next(Object data) {
                    val targetStepIndex = stepIndex + 1;

                    if (targetStepIndex >= preparedSteps.size()) {
                        // this step is the last step
                        throw new IllegalStateException("No further step is available");
                    }

                    val currentStep = preparedSteps.get(stepIndex);
                    val targetStep = preparedSteps.get(targetStepIndex);

                    // transform output data
                    val transformedData = new AtomicReference<>(data);
                    for (val transformator : currentStep.getFollowingTransformers()) {
                        transformedData.updateAndGet(transformator::transform);
                    }

                    cachedInputDataByStepIndex.put(targetStepIndex, transformedData.get());
                    onNextListener.accept(preparedSteps.get(targetStepIndex));
                }

                @Override
                public void previous() {
                    val targetStepIndex = stepIndex - 1;

                    if (targetStepIndex < 0) {
                        // this step is the first step
                        throw new IllegalStateException("No previous step is available");
                    }

                    onPreviousListener.accept(preparedSteps.get(targetStepIndex));
                }
            };
        }

    }
}
