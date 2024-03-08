package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class StepsChainBuilder<TContext> {

    private final ServicesFacade servicesFacade;

    private final List<StepDefinitionWithContext<?, ?, TContext>> registeredSteps = new ArrayList<>();

    private final Consumer<Step> onNextListener;
    private final Consumer<Step> onPreviousListener;

    private final Optional<TContext> context;

    public StepsChainBuilder(
            @NonNull ServicesFacade servicesFacade,
            @NonNull Consumer<Step> onNextListener,
            @NonNull Consumer<Step> onPreviousListener
    ) {
        this.servicesFacade = servicesFacade;
        this.onNextListener = onNextListener;
        this.onPreviousListener = onPreviousListener;
        this.context = Optional.empty();
    }

    public StepsChainBuilder(
            @NonNull ServicesFacade servicesFacade,
            @NonNull Consumer<Step> onNextListener,
            @NonNull Consumer<Step> onPreviousListener,
            @Nullable TContext context
    ) {
        this.servicesFacade = servicesFacade;
        this.onNextListener = onNextListener;
        this.onPreviousListener = onPreviousListener;
        this.context = Optional.ofNullable(context);
    }

    public StepsChainBuilder register(StepDefinition<?, ?> step) {
        registeredSteps.add(step.addContextCompatibility());
        return this;
    }

    public StepsChainBuilder register(StepDefinitionWithContext<?, ?, TContext> step) {
        if (!context.isPresent()) {
            throw new IllegalArgumentException("No context provided");
        }

        registeredSteps.add(step);
        return this;
    }

    public StepChain build() {
        if (!chainIsValid()) {
            throw new IllegalStateException("Steps chain is not valid");
        }

        val preparedSteps = new ArrayList<Step>();
        val indexCurrentStep = new AtomicInteger(0);
        val cachedInputDataByStepIndex = new HashMap<Integer, Object>();

        val totalNrOfSteps = registeredSteps.size();

        for (StepDefinitionWithContext<?, ?, TContext> step : registeredSteps) {
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

                return ((StepDefinitionWithContext.StepViewLoader<Object, Object, TContext>) step.getViewLoader())
                        .load(data, navigator, context.orElse(null), servicesFacade);
            };

            preparedSteps.add(Step.builder()
                    .id(step.getId())
                    .title(step.getTitle())
                    .navigator(navigator)
                    .stepIndex(stepIndex)
                    .viewLoader(viewLoader)
                    .build());
        }

        return new StepChain(Collections.unmodifiableList(preparedSteps));
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
}
