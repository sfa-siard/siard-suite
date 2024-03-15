package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.hooks.HooksCaller;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * A builder class for constructing a chain of steps in a stepper.
 */
@Slf4j
@RequiredArgsConstructor
public class StepsChainBuilder {

    private final ServicesFacade servicesFacade;

    private final Consumer<Step> onNextListener;
    private final Consumer<Step> onPreviousListener;

    /**
     * Registers first step in the steps chain.
     */
    public <TPrevious> StepRegisterer<TPrevious> register(StepDefinition<Void, TPrevious> step) {
        val registerer = new StepRegisterer<Void>(servicesFacade, onNextListener, onPreviousListener);

        return registerer.register(step);
    }

    /**
     * A step registerer class responsible for registering steps and building the step chain type-safe.
     *
     * @param <TOutPrevious> The type of the previous output for the registered steps.
     */
    @RequiredArgsConstructor
    public static class StepRegisterer<TOutPrevious> {

        private final ServicesFacade servicesFacade;
        private final Consumer<Step> onNextListener;
        private final Consumer<Step> onPreviousListener;

        private final AtomicInteger indexNextStep = new AtomicInteger(0);
        private final Map<Integer, Object> cachedInputDataByStepIndex = new HashMap<>();
        private final List<Step> preparedSteps = new ArrayList<>();

        private final HooksCaller hooksCaller = new HooksCaller();

        /**
         * Registers a step in the steps chain.
         *
         * @param step   The step to be registered.
         * @param <TOut> The type of the output for the registered step.
         */
        public <TOut> StepRegisterer<TOut> register(StepDefinition<TOutPrevious, TOut> step) {
            val stepIndex = indexNextStep.getAndIncrement();
            val navigator = createNavigator(stepIndex);

            final Supplier<LoadedView> viewLoader = () -> {
                try {
                    val data = cachedInputDataByStepIndex.get(stepIndex);

                    val casted = (TOutPrevious) data;

                    return hooksCaller.nextView(step.getViewLoader()
                            .load(casted, navigator, servicesFacade));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw ex;
                }
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

        /**
         * Transforms the output of the last registered step. Can be used, if the last output does not match the
         * next input (e.g. provide further data).
         *
         * @param transformer The transformer function.
         * @param <TOut>      The type of the transformed output.
         */
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
         */
        public StepChain build() {
            return new StepChain(Collections.unmodifiableList(preparedSteps), hooksCaller::clear);
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

                    log.info("Navigate {}->{} with data {}", stepIndex, targetStepIndex, transformedData.get());

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

                    log.info("Navigate {}->{}", stepIndex, targetStepIndex);

                    onPreviousListener.accept(preparedSteps.get(targetStepIndex));
                }
            };
        }

    }
}
