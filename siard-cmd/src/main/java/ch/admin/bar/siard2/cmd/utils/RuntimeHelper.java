package ch.admin.bar.siard2.cmd.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuntimeHelper {
    public static String getRuntimeInformation() {
        Runtime rt = Runtime.getRuntime();

        val stringBuilder = new StringBuilder()
                .append("free memory: ")
                .append(rt.freeMemory())
                .append("\n")
                .append("total memory: ")
                .append(rt.totalMemory())
                .append("\n")
                .append("maximum memory: ")
                .append(rt.maxMemory())
                .append("\n")
                .append("System properties: ")
                .append(rt.maxMemory())
                .append("\n");

        val systemProperties = System.getProperties();
        val systemPropertiesEnumeration = systemProperties.propertyNames();
        while (systemPropertiesEnumeration.hasMoreElements()) {
            val key = systemPropertiesEnumeration.nextElement()
                                                 .toString();
            stringBuilder
                    .append("  ")
                    .append(key)
                    .append(": ")
                    .append(systemProperties.getProperty(key))
                    .append("\n");
        }

        return stringBuilder.toString();
    }
}
