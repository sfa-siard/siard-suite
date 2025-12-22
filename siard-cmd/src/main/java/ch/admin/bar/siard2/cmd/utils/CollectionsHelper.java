package ch.admin.bar.siard2.cmd.utils;

import lombok.val;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CollectionsHelper {
    public static <T> Set<T> setOf(final T... entries) {
        val set = new HashSet<>(Arrays.asList(entries));
        return Collections.unmodifiableSet(set);
    }
}
