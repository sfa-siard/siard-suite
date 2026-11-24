package ch.admin.bar.siardsuite.webcore.archive;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Archives are isolated by scope", scenario "An app without exactly one scope
 * resolver does not start": the logic of the startup check, without starting an app (a missing
 * resolver would fail closed anyway, as the filter then cannot resolve any scope).
 */
class ScopeResolverRequiredTest {

    @Test
    void anAppWithoutExactlyOneScopeResolverDoesNotStart() {
        for (long resolvers : new long[]{0, 2}) {
            IllegalStateException e = assertThrows(IllegalStateException.class,
                    () -> ArchiveScopeFilter.requireExactlyOne(resolvers));
            assertTrue(e.getMessage().contains(ScopeResolver.class.getName()), e.getMessage());
            assertTrue(e.getMessage().contains("exactly one"), e.getMessage());
        }
        assertDoesNotThrow(() -> ArchiveScopeFilter.requireExactlyOne(1));
    }
}
