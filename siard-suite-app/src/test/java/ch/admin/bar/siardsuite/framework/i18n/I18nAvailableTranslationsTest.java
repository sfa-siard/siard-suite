package ch.admin.bar.siardsuite.framework.i18n;

import ch.admin.bar.siardsuite.framework.i18n.keys.Key;
import lombok.NonNull;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.admin.bar.siardsuite.framework.i18n.helper.I18nTestHelper.BUNDLES;
import static ch.admin.bar.siardsuite.framework.i18n.helper.I18nTestHelper.extractStaticKeyDefinitions;

class I18nAvailableTranslationsTest {

    @Test
    public void areAllStaticDefinedI18nKeysSupportedByMessageBundles() {
        // given
        val keys = extractStaticKeyDefinitions();

        // when
        val unsupportedKeys = keys.stream()
                .flatMap(key -> {
                    val bundlesWhichNotContainingKey = findBundlesWhichNotContainingKey(key);

                    if (bundlesWhichNotContainingKey.isEmpty()) {
                        return Stream.empty();
                    }

                    return Stream.of(new UnsupportedKey(
                            key,
                            bundlesWhichNotContainingKey.stream()
                                    .map(ResourceBundle::getLocale)
                                    .collect(Collectors.toSet())));
                })
                .collect(Collectors.toSet());

        // then
        Assertions.assertThat(keys).isNotEmpty();
        if (!unsupportedKeys.isEmpty()) {
            System.out.println("========= Properties-template for unsupported keys =========");
            System.out.println(createPropertiesTemplate(unsupportedKeys));
            System.out.println("============================================================");

            Assertions.fail(String.format(
                    "Unsupported i18n-keys found:\n%s",
                    unsupportedKeys.stream()
                            .map(UnsupportedKey::toFailureMessage)
                            .collect(Collectors.joining("\n"))
            ));
        }

        Assertions.assertThat(unsupportedKeys).isEmpty();
    }

    private String createPropertiesTemplate(Set<UnsupportedKey> unsupportedKeys) {
        return unsupportedKeys.stream()
                .map(unsupportedKey -> unsupportedKey.getKey().getValue())
                .sorted()
                .map(keyValue -> keyValue + "=xxx")
                .collect(Collectors.joining("\n"));
    }

    private List<ResourceBundle> findBundlesWhichNotContainingKey(Key key) {
        return BUNDLES.stream()
                .filter(resourceBundle -> !resourceBundle.containsKey(key.getValue()))
                .collect(Collectors.toList());
    }

    @Value
    private static class UnsupportedKey {
        @NonNull Key key;
        @NonNull Set<Locale> unsupportedLanguages;

        public String toFailureMessage() {
            return String.format(
                    "Key '%-50s' is not supported by the following translations: '%s'",
                    key.getValue(),
                    unsupportedLanguages.stream()
                            .map(Locale::getLanguage)
                            .collect(Collectors.joining(", ")));
        }
    }
}