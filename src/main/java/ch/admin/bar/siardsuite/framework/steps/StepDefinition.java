package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;


@Value
@Builder
public class StepDefinition<TIn, TOut> {
    @NonNull Class<TIn> inputType;
    @NonNull Class<TOut> outputType;
    @NonNull StepViewLoader<TIn, TOut> viewLoader;
    @NonNull DisplayableText title;

    public interface StepViewLoader<TIn, TOut> {
        LoadedFxml load(TIn data, StepperNavigator<TOut> navigator, ServicesFacade servicesFacade);
    }

    public interface StepViewLoaderWithoutServices<TIn, TOut> {
        LoadedFxml load(TIn data, StepperNavigator<TOut> navigator);
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
    }
}