package ch.admin.bar.siardsuite.framework.i18n.helper;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.framework.i18n.keys.Key;
import lombok.val;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class I18nTestHelper {
    public static final List<ResourceBundle> BUNDLES = Arrays.asList(
            ResourceBundle.getBundle("ch/admin/bar/siardsuite/i18n/messages", Locale.GERMAN),
            ResourceBundle.getBundle("ch/admin/bar/siardsuite/i18n/messages", Locale.FRENCH),
            ResourceBundle.getBundle("ch/admin/bar/siardsuite/i18n/messages", Locale.ITALIAN),
            ResourceBundle.getBundle("ch/admin/bar/siardsuite/i18n/messages", Locale.ENGLISH)
    );

    public static Set<Key> extractStaticKeyDefinitions() {
        val siardClasses = findAllClasses("ch.admin.bar.siardsuite");

        return siardClasses.stream()
                .flatMap(clazz -> extractStaticKeyDefinitions(clazz).stream())
                .collect(Collectors.toSet());
    }

    private static Set<Key> extractStaticKeyDefinitions(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> Key.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return (Key) field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    private static Set<Class<?>> findAllClasses(String packageName) {
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

    private static Set<String> findAllClassNames(String packageName) {
        val mainBuildOutput = "./build/classes/java/main/".replace("/", File.separator);

        val classNames = findClassNames(new File(mainBuildOutput, packageName.replace(".", File.separator)))
                .collect(Collectors.toList());

        return classNames.stream()
                .map(s -> s.replace(mainBuildOutput, ""))
                .map(s -> s.replace(".class", ""))
                .map(s -> s.replace(File.separator, "."))
                .collect(Collectors.toSet());
    }

    private static Stream<String> findClassNames(File parentFile) {
        val childFiles = parentFile.listFiles();
        val classes = Arrays.stream(childFiles)
                .filter(File::isFile)
                .filter(childFile -> childFile.getName().endsWith(".class"))
                .map(File::toString);

        val classesFromSubDirs = Arrays.stream(childFiles)
                .filter(File::isDirectory)
                .flatMap(I18nTestHelper::findClassNames);

        return Stream.concat(classes, classesFromSubDirs);
    }
}