package ch.admin.bar.siard2.cmd.utils.siard.update;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * The {@code Updater} class is responsible for applying a set of {@link UpdateInstruction}s to objects of
 * various types. It allows updating specific fields or properties of an object based on the provided
 * instructions.
 */
@Builder
public class Updater {

    @NonNull
    @Singular
    private final List<UpdateInstruction<?>> instructions;

    /**
     * Applies the configured update instructions to the given object.
     */
    public <T> T applyUpdate(T original) {
        Function<T, T> overrider = (Function<T, T>) findUpdaterForType(original.getClass());
        return overrider.apply(original);
    }

    private <T> Function<T, T> findUpdaterForType(final Class<T> type) {
        final Optional<Function<T, T>> overriderOptional = instructions.stream()
                .filter(instruction -> instruction.getClazz().equals(type))
                .map(instruction -> (Function<T, T>) instruction.getUpdater())
                .reduce(Function::andThen);

        final Function<T, T> overrideNothing = t -> t;

        return overriderOptional.orElse(overrideNothing);
    }
}
