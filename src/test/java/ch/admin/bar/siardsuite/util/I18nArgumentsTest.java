package ch.admin.bar.siardsuite.util;

import ch.admin.bar.siardsuite.util.helper.I18nTestHelper;
import ch.admin.bar.siardsuite.util.helper.StringTestHelper;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKeyArg;
import ch.admin.bar.siardsuite.util.i18n.keys.Key;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class I18nArgumentsTest {

    @Test
    public void containTheValuesOfAllStaticDefinedI18nKeyNoPlaceholders() {
        // given
        val keys = I18nTestHelper.extractStaticKeyDefinitions().stream()
                .flatMap(key -> CastHelper.tryCastWithStream(key, I18nKey.class))
                .collect(Collectors.toSet());

        // when
        val values = keys.stream()
                .flatMap(key -> findValues(key).stream())
                .collect(Collectors.toList());

        // then
        Assertions.assertThat(keys).isNotEmpty();
        Assertions.assertThat(values).isNotEmpty();

        val valuesWithWrongAmountOfPlaceholders = values.stream()
                .filter(keyWithValue -> StringTestHelper.getNumberOfPlaceholders(keyWithValue.getText()) != 0)
                .collect(Collectors.toList());

        Assertions.assertThat(valuesWithWrongAmountOfPlaceholders).isEmpty();
    }

    @Test
    public void containTheValuesOfAllStaticDefinedI18nKeyArgOnePlaceholder() {
        // given
        val keys = I18nTestHelper.extractStaticKeyDefinitions().stream()
                .flatMap(key -> CastHelper.tryCastWithStream(key, I18nKeyArg.class))
                .collect(Collectors.toSet());

        // when
        val values = keys.stream()
                .flatMap(key -> findValues(key).stream())
                .collect(Collectors.toList());

        // then
        Assertions.assertThat(keys).isNotEmpty();
        Assertions.assertThat(values).isNotEmpty();

        val valuesWithWrongAmountOfPlaceholders = values.stream()
                .filter(keyWithValue -> StringTestHelper.getNumberOfPlaceholders(keyWithValue.getText()) != 1)
                .collect(Collectors.toList());

        Assertions.assertThat(valuesWithWrongAmountOfPlaceholders).isEmpty();
    }

    private Set<KeyWithValue> findValues(Key key) {
        return I18nTestHelper.BUNDLES.stream()
                .map(resourceBundle -> new KeyWithValue(
                        key,
                        resourceBundle.getString(key.getValue()),
                        resourceBundle.getLocale()))
                .collect(Collectors.toSet());
    }

    @Value
    private static class KeyWithValue {
        Key key;
        String text;
        Locale locale;
    }
}