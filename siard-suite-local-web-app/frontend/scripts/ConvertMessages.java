import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * One-time conversion of the desktop app's messages_*.properties into the frontend's i18n JSON
 * files (design D12). Run from the frontend directory:
 *
 * <pre>
 * java scripts/ConvertMessages.java \
 *     ../../siard-suite-app/src/main/resources/ch/admin/bar/siardsuite/i18n src/i18n \
 *     welcome.text menu.item.info info.dialog.title info.dialog.text
 * </pre>
 *
 * It overwrites src/i18n/{de,fr,it,en}.json with exactly the given keys, so texts added to the
 * JSON by hand afterwards would be lost: after the migration, edit the JSON files directly.
 * <p>
 * TODO: delete this script once the web app covers the full functionality of the desktop app
 * and siard-suite-app with its messages_*.properties is retired; the JSON files are the only
 * source of the texts from then on.
 * <ul>
 *   <li>The dotted desktop keys become nested JSON objects ({@code info.dialog.title} is
 *       {@code info → dialog → title}), which is what i18next's default key separator reads, so
 *       code keeps using the desktop key names. A key that is also the prefix of another
 *       requested key cannot be nested as it is, so it gets the suffix {@code .default} and
 *       the longer key keeps its name: {@code button.save} becomes {@code button.save.default}
 *       next to {@code button.save.archive}. Each rename is printed; code uses the renamed key.</li>
 *   <li>Properties.load(InputStream) reads ISO-8859-1 (the de/fr/it files), {@code \}uXXXX
 *       escapes (the en file) and line continuations, exactly as the desktop app does.</li>
 *   <li>Placeholders become named i18next arguments: sequential {@code %s}/{@code %d} become
 *       {@code {{arg0}}}, {@code {{arg1}}}, ...; positional {@code %n$s} becomes
 *       {@code {{arg<n-1>}}}; {@code %%} becomes {@code %}. Call sites pass {@code arg0} etc.</li>
 * </ul>
 */
public class ConvertMessages {

    private static final List<String> LANGUAGES = List.of("de", "fr", "it", "en");
    private static final Pattern PLACEHOLDER = Pattern.compile("%(?:(\\d+)\\$)?([sd%])");

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("usage: java ConvertMessages.java <properties dir> <json dir> <key>...");
            System.exit(2);
        }
        Path source = Path.of(args[0]);
        Path target = Path.of(args[1]);
        List<String> keys = Arrays.asList(args).subList(2, args.length);
        Map<String, String> jsonKeys = jsonKeys(keys);
        jsonKeys.forEach((key, jsonKey) -> {
            if (!key.equals(jsonKey)) {
                System.out.println("renamed " + key + " -> " + jsonKey);
            }
        });
        Files.createDirectories(target);

        for (String language : LANGUAGES) {
            Properties messages = new Properties();
            try (InputStream in = Files.newInputStream(source.resolve("messages_" + language + ".properties"))) {
                messages.load(in);
            }
            Map<String, Object> tree = new LinkedHashMap<>();
            for (String key : keys) {
                String value = messages.getProperty(key);
                if (value == null) {
                    throw new IllegalArgumentException("key " + key + " missing in messages_" + language);
                }
                put(tree, jsonKeys.get(key).split("\\."), toI18next(value));
            }
            StringBuilder json = new StringBuilder();
            write(json, tree, "");
            json.append("\n");
            Files.writeString(target.resolve(language + ".json"), json, StandardCharsets.UTF_8);
            System.out.println("wrote " + target.resolve(language + ".json"));
        }
    }

    // desktop key -> JSON key: a key that is also the prefix of another one gets ".default"
    static Map<String, String> jsonKeys(List<String> keys) {
        Map<String, String> jsonKeys = new LinkedHashMap<>();
        for (String key : keys) {
            boolean isPrefix = keys.stream().anyMatch(other -> other.startsWith(key + "."));
            String jsonKey = isPrefix ? key + ".default" : key;
            if (isPrefix && keys.contains(jsonKey)) {
                throw new IllegalArgumentException("cannot rename " + key + ": " + jsonKey + " exists already");
            }
            jsonKeys.put(key, jsonKey);
        }
        return jsonKeys;
    }

    @SuppressWarnings("unchecked")
    private static void put(Map<String, Object> tree, String[] path, String value) {
        Map<String, Object> node = tree;
        for (int i = 0; i < path.length - 1; i++) {
            node = (Map<String, Object>) node.computeIfAbsent(path[i], segment -> new LinkedHashMap<String, Object>());
        }
        node.put(path[path.length - 1], value);
    }

    // 2-space indented like Prettier, so the files pass the formatting check unchanged
    @SuppressWarnings("unchecked")
    private static void write(StringBuilder json, Map<String, Object> node, String indent) {
        json.append("{\n");
        int i = 0;
        for (Map.Entry<String, Object> entry : node.entrySet()) {
            json.append(indent).append("  ").append(quote(entry.getKey())).append(": ");
            if (entry.getValue() instanceof Map<?, ?> child) {
                write(json, (Map<String, Object>) child, indent + "  ");
            } else {
                json.append(quote((String) entry.getValue()));
            }
            json.append(++i < node.size() ? ",\n" : "\n");
        }
        json.append(indent).append("}");
    }

    static String toI18next(String value) {
        Matcher matcher = PLACEHOLDER.matcher(value);
        StringBuilder result = new StringBuilder();
        int sequential = 0;
        while (matcher.find()) {
            String replacement;
            if (matcher.group(2).equals("%")) {
                replacement = "%";
            } else if (matcher.group(1) != null) {
                replacement = "{{arg" + (Integer.parseInt(matcher.group(1)) - 1) + "}}";
            } else {
                replacement = "{{arg" + sequential++ + "}}";
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String quote(String text) {
        StringBuilder quoted = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"' -> quoted.append("\\\"");
                case '\\' -> quoted.append("\\\\");
                case '\n' -> quoted.append("\\n");
                case '\r' -> quoted.append("\\r");
                case '\t' -> quoted.append("\\t");
                default -> {
                    if (c < 0x20) {
                        quoted.append(String.format("\\u%04x", (int) c));
                    } else {
                        quoted.append(c);
                    }
                }
            }
        }
        return quoted.append('"').toString();
    }
}
