package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;


@Value
@Builder
public class StepDefinition<TIn, TOut> {
    /**
     * If empty, step will be invisible in the header
     */
    @NonNull
    @Builder.Default
    Optional<I18nKey> title = Optional.empty();

    @NonNull Class<TIn> inputType;
    @NonNull Class<TOut> outputType;
    @NonNull StepViewLoader<TIn, TOut> viewLoader;

    public boolean isVisible() {
        return title.isPresent();
    }

    public interface StepViewLoader<TIn, TOut> {
        LoadedFxml load(TIn data, StepperNavigator<TOut> navigator, ServicesFacade servicesFacade);
    }

    public interface StepViewLoaderWithoutServices<TIn, TOut> {
        LoadedFxml load(TIn data, StepperNavigator<TOut> navigator);
    }

    public interface StepViewLoaderWithoutData<TIn, TOut> {
        LoadedFxml load(StepperNavigator<TOut> navigator, ServicesFacade servicesFacade);
    }

    public interface StepViewLoaderWithoutDataAndServices<TIn, TOut> {
        LoadedFxml load(StepperNavigator<TOut> navigator);
    }

    public static class StepDefinitionBuilder<TIn, TOut> {
        public StepDefinitionBuilder<TIn, TOut> viewLoader(StepViewLoader<TIn, TOut> viewLoader) {
            this.viewLoader = viewLoader;
            return this;
        }

        public StepDefinitionBuilder<TIn, TOut> viewLoader(StepViewLoaderWithoutServices<TIn, TOut> viewLoader) {
            this.viewLoader = (data, navigator, servicesRegistry) -> viewLoader.load(data, navigator);
            return this;
        }

        public StepDefinitionBuilder<TIn, TOut> viewLoader(StepViewLoaderWithoutData<TIn, TOut> viewLoader) {
            this.viewLoader = (data, navigator, servicesRegistry) -> viewLoader.load(navigator, servicesRegistry);
            return this;
        }

        public StepDefinitionBuilder<TIn, TOut> viewLoader(StepViewLoaderWithoutDataAndServices<TIn, TOut> viewLoader) {
            this.viewLoader = (data, navigator, servicesRegistry) -> viewLoader.load(navigator);
            return this;
        }

        public StepDefinitionBuilder<TIn, TOut> title(I18nKey title) {
            this.title$value = Optional.of(title);
            this.title$set = true;
            return this;
        }
    }
}