package ch.admin.bar.siardsuite.webcore.archive;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

/** A lock timeout of one second, so that a lock that was not released shows as 503 at once. */
public class ShortLockTimeoutProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("siard.archives.lock-timeout", "PT1S");
    }
}
