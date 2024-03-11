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

/**
 * Builder for constructing a chain of steps in a stepper, each step defined with or without context.
 * <p>
 * This builder allows the registration of {@link StepDefinition} or {@link StepDefinitionWithContext}
 * instances to create a sequence of steps in a stepper. It provides methods to register steps,
 * build the step chain, and perform checks on the validity of the steps chain.
 *
 * @param <TContext> The type of context used in {@link StepDefinitionWithContext}.
 */
@RequiredArgsConstructor
public class StepsChainBuilder<TContext> {

    private final ServicesFacade servicesFacade;

    private final List<StepDefinitionWithContext<?, ?, TContext>> registeredSteps = new ArrayList<>();

    private final Consumer<Step> onNextListener;
    private final Consumer<Step> onPreviousListener;

    private final Optional<TContext> context;

    /**
     * Constructs a StepsChainBuilder with the required parameters.
     *
     * @param servicesFacade    The services facade providing access to various services.
     * @param onNextListener    Listener for the next step event.
     * @param onPreviousListener Listener for the previous step event.
     */
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

    /**
     * Constructs a StepsChainBuilder with the required parameters including a context.
     *
     * @param servicesFacade    The services facade providing access to various services.
     * @param onNextListener    Listener for the next step event.
     * @param onPreviousListener Listener for the previous step event.
     * @param context           The context to be used in {@link StepDefinitionWithContext}.
     */
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

    /**
     * Registers a step without context in the steps chain.
     *
     * @param step The step to be registered.
     * @return The current builder instance for method chaining.
     */
    public StepsChainBuilder register(StepDefinition<?, ?> step) {
        registeredSteps.add(step.addContextCompatibility());
        return this;
    }

    /**
     * Registers a step with context in the steps chain.
     *
     * @param step The step to be registered.
     * @return The current builder instance for method chaining.
     */
    public StepsChainBuilder register(StepDefinitionWithContext<?, ?, TContext> step) {
        if (!context.isPresent()) {
            throw new IllegalArgumentException("No context provided");
        }

        registeredSteps.add(step);
        return this;
    }

    /**
     * Builds the step chain based on the registered steps.
     *
     * @return The constructed {@link StepChain}.
     * @throws IllegalStateException If the steps chain is not valid.
     */
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
