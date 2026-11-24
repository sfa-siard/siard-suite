package ch.admin.bar.siardsuite.localwebapp.connections;

import ch.admin.bar.siardsuite.localwebapp.database.ServerBasedDbms;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One entry of the DBMS catalog served by {@code GET /api/dbms}: enough for the details form
 * without exposing the JDBC encoder functions (design D5).
 *
 * @param id       stable DBMS identifier used in requests and saved profiles
 * @param name     display name
 * @param kind     connection kind; {@code "server"} for the network DBMS of this change
 * @param fields   form fields the details page renders, in display order
 * @param examples placeholder values per field key
 */
public record DbmsCatalogEntry(
        String id,
        String name,
        String kind,
        List<FieldDescriptor> fields,
        Map<String, String> examples) {

    public static final String KIND_SERVER = "server";

    /**
     * A form field of the details page.
     *
     * @param key      field key, also the name of the corresponding request and profile property
     * @param type     {@code "text"} or {@code "password"}
     * @param required whether the field must be filled in
     */
    public record FieldDescriptor(String key, String type, boolean required) {

        public static final String TYPE_TEXT = "text";
        public static final String TYPE_PASSWORD = "password";
    }

    private static final List<FieldDescriptor> SERVER_FIELDS = List.of(
            new FieldDescriptor("host", FieldDescriptor.TYPE_TEXT, true),
            new FieldDescriptor("port", FieldDescriptor.TYPE_TEXT, true),
            new FieldDescriptor("dbName", FieldDescriptor.TYPE_TEXT, true),
            new FieldDescriptor("user", FieldDescriptor.TYPE_TEXT, true),
            new FieldDescriptor("password", FieldDescriptor.TYPE_PASSWORD, false),
            new FieldDescriptor("options", FieldDescriptor.TYPE_TEXT, false));

    public static DbmsCatalogEntry of(ServerBasedDbms dbms) {
        Map<String, String> examples = new LinkedHashMap<>();
        examples.put("host", dbms.exampleHost());
        examples.put("port", dbms.examplePort());
        examples.put("dbName", dbms.exampleDbName());
        return new DbmsCatalogEntry(dbms.id(), dbms.name(), KIND_SERVER, SERVER_FIELDS, examples);
    }
}
