package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


/**
 * Represents the definition of a step in a stepper-view.
 *
 * @param <TIn>  The input data type for the step.
 * @param <TOut> The output data type for the step.
 */
@Value
public class StepDefinition<TIn, TOut> {
    /**
     * If empty, step will be invisible in the header
     */
    Optional<I18nKey> title;

    StepViewLoader<TIn, TOut> viewLoader;

    public StepDefinition(
            @Nullable I18nKey title,
            @NonNull StepViewLoader<TIn, TOut> viewLoader
    ) {
        this.title = Optional.ofNullable(title);
        this.viewLoader = viewLoader;
    }

    public StepDefinition(
            @NonNull StepViewLoader<TIn, TOut> viewLoader
    ) {
        this.title = Optional.empty();
        this.viewLoader = viewLoader;
    }

    public StepDefinition(
            @Nullable I18nKey title,
            @NonNull StepViewLoaderWithoutServices<TIn, TOut> viewLoader
    ) {
        this.title = Optional.ofNullable(title);
        this.viewLoader = (data, navigator, servicesFacade) -> viewLoader.load(data, navigator);
    }

    public StepDefinition(
            @NonNull StepViewLoaderWithoutServices<TIn, TOut> viewLoader
    ) {
        this.title = Optional.empty();
        this.viewLoader = (data, navigator, servicesFacade) -> viewLoader.load(data, navigator);
    }

    public StepDefinition(
            @Nullable I18nKey title,
            @NonNull StepViewLoaderWithoutData<TIn, TOut> viewLoader
    ) {
        this.title = Optional.ofNullable(title);
        this.viewLoader = (data, navigator, servicesFacade) -> viewLoader.load(navigator, servicesFacade);
    }

    public StepDefinition(
            @NonNull StepViewLoaderWithoutData<TIn, TOut> viewLoader
    ) {
        this.title = Optional.empty();
        this.viewLoader = (data, navigator, servicesFacade) -> viewLoader.load(navigator, servicesFacade);
    }

    public interface StepViewLoader<TIn, TOut> {
        LoadedView load(TIn data, StepperNavigator<TOut> navigator, ServicesFacade servicesFacade);
    }

    public interface StepViewLoaderWithoutServices<TIn, TOut> {
        LoadedView load(TIn data, StepperNavigator<TOut> navigator);
    }

    public interface StepViewLoaderWithoutData<TIn, TOut> {
        LoadedView load(StepperNavigator<TOut> navigator, ServicesFacade servicesFacade);
    }
}