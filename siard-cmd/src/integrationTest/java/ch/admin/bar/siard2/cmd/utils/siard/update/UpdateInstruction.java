package ch.admin.bar.siard2.cmd.utils.siard.update;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

/**
 * Represents an update instruction which can be applied to a specific type.
 * An update instruction consists of the target class and a function to apply updates to instances of that class.
 *
 * @param <T> The type of objects that this update instruction can update, extending the {@link Updatable} interface.
 */
@Value
@Builder
public class UpdateInstruction<T> {
    /**
     * The class representing the type of objects that this update instruction can update.
     */
    @NonNull Class<T> clazz;

    /**
     * The function that applies updates to instances of the specified type.
     */
    @NonNull Function<T, T> updater;
}