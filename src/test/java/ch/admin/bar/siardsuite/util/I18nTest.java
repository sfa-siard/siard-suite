package ch.admin.bar.siardsuite.util;

import ch.admin.bar.siardsuite.SiardApplication;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class I18nTest {

    private static final List<ResourceBundle> BUNDLES = Arrays.asList(
            ResourceBundle.getBundle("ch/admin/bar/siardsuite/i18n/messages", Locale.GERMAN),
            ResourceBundle.getBundle("ch/admin/bar/siardsuite/i18n/messages", Locale.FRENCH),
            ResourceBundle.getBundle("ch/admin/bar/siardsuite/i18n/messages", Locale.ITALIAN),
            ResourceBundle.getBundle("ch/admin/bar/siardsuite/i18n/messages", Locale.ENGLISH)
    );

    @SneakyThrows
    @Test
    public void areAllStaticDefinedI18nKeysSupportedByMessageBundles() {
        // given
        val keys = extractStaticI18nKeyDefinitions();

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

    private List<ResourceBundle> findBundlesWhichNotContainingKey(I18nKey key) {
        return BUNDLES.stream()
                .filter(resourceBundle -> !resourceBundle.containsKey(key.getValue()))
                .collect(Collectors.toList());
    }

    private Set<I18nKey> extractStaticI18nKeyDefinitions() {
        val siardClasses = findAllClasses("ch.admin.bar.siardsuite");

        return siardClasses.stream()
                .flatMap(clazz -> extractStaticI18nKeyDefinitions(clazz).stream())
                .collect(Collectors.toSet());
    }

    private Set<I18nKey> extractStaticI18nKeyDefinitions(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.getType().equals(I18nKey.class))
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return (I18nKey) field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    private Set<Class<?>> findAllClasses(String packageName) {
        return findAllClassNames(packageName).stream()
                .map(className -> {
                    try {
                        return SiardApplication.class.getClassLoader().loadClass(className);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    private Set<String> findAllClassNames(String packageName) {
        val mainBuildOutput = "./build/classes/java/main";

        return findClassNames(new File(mainBuildOutput, packageName.replaceAll("[.]", "/")))
                .map(s -> s.replaceFirst("./build/classes/java/main/", ""))
                .map(s -> s.replace(mainBuildOutput, ""))
                .map(s -> s.replace(".class", ""))
                .map(s -> s.replaceAll("/", "."))
                .collect(Collectors.toSet());
    }

    private Stream<String> findClassNames(File parentFile) {
        val childFiles = parentFile.listFiles();
        val classes = Arrays.stream(childFiles)
                .filter(File::isFile)
                .filter(childFile -> childFile.getName().endsWith(".class"))
                .map(File::toString);

        val classesFromSubDirs = Arrays.stream(childFiles)
                .filter(File::isDirectory)
                .flatMap(this::findClassNames);

        return Stream.concat(classes, classesFromSubDirs);
    }

    @Value
    private static class UnsupportedKey {
        @NonNull I18nKey key;
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