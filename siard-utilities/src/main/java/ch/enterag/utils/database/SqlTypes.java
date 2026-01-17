/*
Copyright  : 2014, Enter AG, Zurich, Switzerland
             2024, Puzzle ITC GmbH, Switzerland
Created    : 12.12.2014, Hartwig Thomas
*/
package ch.enterag.utils.database;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/**
 * This class maps values of java.sql.Types to their name.
 *
 * @author Hartwig Thomas
 */
public abstract class SqlTypes {
    /**
     * Used for values of SQL Types that do not have a corresponding name.
     */
    private static final String UNKNOWN = "UNKNOWN";

    /**
     * Maps values of SQL Types to their name.
     */
    private static final Map<Integer, String> mapTypeNames = new HashMap<>();

    static {
      initialize();
    }

    /**
     * Reads the fields of java.sql.Types and populates a map with field values and names.
     */
    private static void initialize() {
        Field[] fields = Types.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                mapTypeNames.put(field.getInt(null), field.getName());
            } catch (IllegalAccessException e) {
                System.err.println("Failed to access field: " + field.getName());
            }
        }
    }

    /**
     * Translates the SQL type to its name.
     *
     * @param typeValue java.sql.Types value.
     * @return Name of the type.
     */
    public static String getTypeName(int typeValue) {
      return mapTypeNames.getOrDefault(typeValue, UNKNOWN);
    }

    /**
     * Returns a list of all SQL type values.
     *
     * @return A sorted list of all types.
     */
    public static List<Integer> getAllTypes() {
        List<Integer> listTypes = new ArrayList<>(mapTypeNames.keySet());
        Collections.sort(listTypes);
        return listTypes;
    }
}
