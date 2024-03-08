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
public class StepDefinitionWithContext<TIn, TOut, TContext> {
    /**
     * If empty, step will be invisible in the header
     */
    @NonNull
    @Builder.Default
    Optional<I18nKey> title = Optional.empty();

    @NonNull Class<TIn> inputType;
    @NonNull Class<TOut> outputType;
    @NonNull StepViewLoader<TIn, TOut, TContext> viewLoader;

    public StepId getId() {
        return StepId.builder()
                .title(title)
                .inputType(inputType)
                .outputType(outputType)
                .build();
    }

    public interface StepViewLoader<TIn, TOut, TContext> {
        LoadedFxml load(TIn data, StepperNavigator<TOut> navigator, TContext context, ServicesFacade servicesFacade);
    }

    public interface StepViewLoaderWithoutData<TIn, TOut, TContext> {
        LoadedFxml load(StepperNavigator<TOut> navigator, TContext context, ServicesFacade servicesFacade);
    }

    public static class StepDefinitionWithContextBuilder<TIn, TOut, TContext> {
        public StepDefinitionWithContextBuilder<TIn, TOut, TContext> viewLoader(StepViewLoader<TIn, TOut, TContext> viewLoader) {
            this.viewLoader = viewLoader;
            return this;
        }

        public StepDefinitionWithContextBuilder<TIn, TOut, TContext> viewLoader(StepViewLoaderWithoutData<TIn, TOut, TContext> viewLoader) {
            this.viewLoader = (data, navigator, context, servicesFacade) -> viewLoader.load(navigator, context, servicesFacade);
            return this;
        }

        public StepDefinitionWithContextBuilder<TIn, TOut, TContext> title(I18nKey title) {
            this.title$value = Optional.of(title);
            this.title$set = true;
            return this;
        }

        public StepDefinitionWithContextBuilder<TIn, TOut, TContext> title(Optional<I18nKey> title) {
            this.title$value = title;
            this.title$set = true;
            return this;
        }
    }
}
