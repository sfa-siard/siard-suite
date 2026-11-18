package ch.admin.bar.siardsuite.webcore;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Spec requirements "Backend core carries no archiving or driver code" and "Backend core carries
 * no local-agent code" (design R3).
 */
class CoreBoundaryTest {

    private static final String LOCAL_PACKAGE = "ch.admin.bar.siardsuite.localwebapp";

    @Test
    void noJdbcDriverIsRegistered() {
        List<String> drivers = Collections.list(DriverManager.getDrivers()).stream()
                .map(driver -> driver.getClass().getName())
                .toList();
        assertEquals(List.of(), drivers);
    }

    @Test
    void coreClassesDoNotReferenceTheLocalPackage() throws IOException {
        String classesDirs = System.getProperty("webcore.classesDirs");
        List<Path> classFiles = new ArrayList<>();
        for (String dir : classesDirs.split(File.pathSeparator)) {
            Path root = Path.of(dir);
            if (Files.isDirectory(root)) {
                try (Stream<Path> files = Files.walk(root)) {
                    files.filter(file -> file.toString().endsWith(".class")).forEach(classFiles::add);
                }
            }
        }
        assertFalse(classFiles.isEmpty(), "no compiled core classes found in " + classesDirs);
        for (Path classFile : classFiles) {
            // class references are stored in internal form, string constants in dotted form
            String content = new String(Files.readAllBytes(classFile), StandardCharsets.ISO_8859_1);
            assertFalse(content.contains(LOCAL_PACKAGE.replace('.', '/')) || content.contains(LOCAL_PACKAGE),
                    classFile + " references " + LOCAL_PACKAGE);
        }
    }
}
