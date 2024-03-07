package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StepsChainBuilder {

    private final Consumer<LoadedFxml> displayService;
    private final ServicesFacade servicesFacade;

    private final Set<StepDefinition<?, ?>> registeredSteps = new HashSet<>();

    public StepsChainBuilder register(StepDefinition<?, ?> step) {
        registeredSteps.add(step);
        return this;
    }

    @Value
    @Builder
    private static class PreparedStep {
        StepDefinition definition;
        StepperNavigator navigator;
        AtomicReference dataCache;
        int stepIndex;
    }

    @Value
    @Builder
    public static class StepMetaData {
        int index;
        DisplayableText title;
    }

    public List<StepMetaData> build() {
        val preparedSteps = new ArrayList<PreparedStep>();
        val indexCurrentStep = new AtomicInteger(0);
        val chain = buildChain();

        for (val step : chain) {
            val stepIndex = indexCurrentStep.getAndIncrement();
            val totalNrOfSteps = chain.size();

            val navigator = new StepperNavigator() {
                @Override
                public void next(Object data) {
                    val targetStepIndex = stepIndex + 1;

                    if (targetStepIndex >= totalNrOfSteps) {
                        // this step is the last step
                        throw new IllegalStateException("No further step is available");
                    }

                    val targetStep = preparedSteps.get(targetStepIndex);
                    targetStep.getDataCache().set(data);
                    displayService.accept((LoadedFxml) targetStep.getDefinition()
                            .getViewLoader()
                            .load(data,
                                    targetStep.getNavigator(),
                                    servicesFacade));
                }

                @Override
                public void previous() {
                    val targetStepIndex = stepIndex - 1;

                    if (targetStepIndex < 0) {
                        // this step is the first step
                        throw new IllegalStateException("No previous step is available");
                    }

                    val targetStep = preparedSteps.get(targetStepIndex);
                    displayService.accept((LoadedFxml) targetStep.getDefinition()
                            .getViewLoader()
                            .load(targetStep.getDataCache().get(),
                                    targetStep.getNavigator(),
                                    servicesFacade));
                }
            };

            preparedSteps.add(PreparedStep.builder()
                    .definition(step)
                    .dataCache(new AtomicReference<>())
                    .navigator(navigator)
                    .stepIndex(stepIndex)
                    .build());
        }

        // display first step
        val firstStep = preparedSteps.get(0);
        displayService.accept((LoadedFxml) firstStep.getDefinition()
                .getViewLoader()
                .load(null,
                        firstStep.getNavigator(),
                        servicesFacade));

        return preparedSteps.stream()
                .map(preparedStep -> StepMetaData.builder()
                        .title(preparedStep.getDefinition().getTitle())
                        .index(preparedStep.getStepIndex())
                        .build())
                .collect(Collectors.toList());
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
