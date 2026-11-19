package ch.admin.bar.siardsuite.webcore.archive;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

/** A row budget of 5000 and a time budget high enough never to stop a scan (design D13). */
public class SmallSearchBudgetProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("siard.archives.search.max-rows", "5000", "siard.archives.search.max-time", "PT1H");
    }
}
