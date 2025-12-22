package ch.admin.bar.siard2.cmd.utils.siard.update;

/**
 * Interface for objects that can apply updates using an {@link Updater}.
 *
 * @param <T> The type of the object that results from applying updates.
 */
public interface Updatable<T> {

    /**
     * Applies updates to the current object using the provided {@link Updater}.
     *
     * @param updater The updater containing the update instructions.
     * @return A new object resulting from applying updates.
     */
    T applyUpdates(final Updater updater);
}

