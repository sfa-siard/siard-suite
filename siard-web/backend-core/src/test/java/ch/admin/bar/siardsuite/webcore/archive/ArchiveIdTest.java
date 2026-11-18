package ch.admin.bar.siardsuite.webcore.archive;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Archives are referenced by opaque ids", scenario "Generated ids are unique".
 */
class ArchiveIdTest {

    @Test
    void generatedIdsAreUnique() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String id = ArchiveId.random().value();
            assertEquals(22, id.length());
            assertTrue(id.matches("[A-Za-z0-9_-]+"), id);
            ids.add(id);
        }
        assertEquals(1000, ids.size());
    }
}
