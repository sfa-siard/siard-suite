package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Builder
@RequiredArgsConstructor
public class StepsChainBuilder {

    private final ServicesFacade servicesFacade;

    private final List<StepDefinition<?, ?>> registeredSteps = new ArrayList<>();

    private final Consumer<Step> onNextListener;
    private final Consumer<Step> onPreviousListener;

    public StepsChainBuilder register(StepDefinition<?, ?> step) {
        registeredSteps.add(step);
        return this;
    }

    public List<Step> build() {
        if (!chainIsValid()) {
            throw new IllegalStateException("Steps chain is not valid");
        }

        val preparedSteps = new ArrayList<Step>();
        val indexCurrentStep = new AtomicInteger(0);
        val cachedInputDataByStepIndex = new HashMap<Integer, Object>();

        val totalNrOfSteps = registeredSteps.size();

        for (StepDefinition step : registeredSteps) {
            val stepIndex = indexCurrentStep.getAndIncrement();

            val navigator = new StepperNavigator() {
                @Override
                public void next(Object data) {
                    val targetStepIndex = stepIndex + 1;

                    if (targetStepIndex >= totalNrOfSteps) {
                        // this step is the last step
                        throw new IllegalStateException("No further step is available");
                    }

                    cachedInputDataByStepIndex.put(targetStepIndex, data);
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

            final Supplier<LoadedFxml> viewLoader = () -> {
                val data = cachedInputDataByStepIndex.get(stepIndex);
                return step.getViewLoader()
                        .load(data, navigator, servicesFacade);
            };

            preparedSteps.add(Step.builder()
                    .definition(step)
                    .navigator(navigator)
                    .stepIndex(stepIndex)
                    .viewLoader(viewLoader)
                    .build());
        }

        return Collections.unmodifiableList(preparedSteps);
    }

    private boolean chainIsValid() {
        AtomicReference<Class> lastOutputType = new AtomicReference<>(Void.class);

        return registeredSteps.stream()
                .allMatch(stepDefinition -> {
                    val matchingInputType = stepDefinition.getInputType().equals(lastOutputType.get());
                    lastOutputType.set(stepDefinition.getOutputType());

                    return matchingInputType;
                });
    }

    private List<StepDefinition<?, ?>> buildChain() {
        val chain = new ArrayList<StepDefinition<?, ?>>();

        StepDefinition<?, ?> currentStep = findStepByInputType(Void.class);
        chain.add(currentStep);

        while (!currentStep.getOutputType().equals(Void.class)) {
            currentStep = findStepByInputType(currentStep.getOutputType());
            chain.add(currentStep);
        }

        return chain;
    }

    private <TIn> StepDefinition<TIn, ?> findStepByInputType(final Class<TIn> inputType) {
        val matchingSteps = registeredSteps.stream()
                .filter(step -> step.getInputType().equals(inputType))
                .collect(Collectors.toList());

        if (matchingSteps.size() != 1) {
            throw new IllegalStateException(String.format(
                    "No unique step found with input type %s. Found steps are: %s",
                    inputType.getCanonicalName(),
                    matchingSteps.stream()
                            .map(StepDefinition::toString)
                            .collect(Collectors.joining(","))));
        }

        return (StepDefinition<TIn, ?>) matchingSteps.get(0);
    }
}
