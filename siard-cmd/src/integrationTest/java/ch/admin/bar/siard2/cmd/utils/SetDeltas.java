package ch.admin.bar.siard2.cmd.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the deltas (differences) between two sets.
 * <p>
 * The deltas include elements that are present in both sets (intersection), elements present only in the first set,
 * and elements present only in the second set.
 *
 * @param <T> The type of elements in the sets.
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SetDeltas<T> {
    Set<T> inBoth;
    Set<T> justInA;
    Set<T> justInB;

    /**
     * Checks if there are deltas available, i.e., if there are differences between the two sets.
     *
     * @return {@code true} if there are deltas, {@code false} otherwise.
     */
    public boolean deltasAvailable() {
        return !justInA.isEmpty() || !justInB.isEmpty();
    }

    /**
     * Finds and returns the deltas (differences) between two sets.
     *
     * @param a The first set.
     * @param b The second set.
     * @param <T> The type of elements in the sets.
     * @return The deltas between the two sets.
     */
    public static <T> SetDeltas<T> findDeltas(final Set<T> a, final Set<T> b) {
        final Set<T> inBoth = new HashSet<>();
        final Set<T> justInA = new HashSet<>();
        final Set<T> justInB = new HashSet<>();

        a.forEach(entry -> {
            if (b.contains(entry)) {
                inBoth.add(entry);
            } else {
                justInA.add(entry);
            }
        });

        b.forEach(entry -> {
            if (!a.contains(entry)) {
                justInB.add(entry);
            }
        });

        return new SetDeltas<>(inBoth, justInA, justInB);
    }
}
