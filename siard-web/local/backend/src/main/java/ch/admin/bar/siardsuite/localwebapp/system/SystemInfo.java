package ch.admin.bar.siardsuite.localwebapp.system;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

/**
 * Version and runtime of the agent, as shown in the About view.
 *
 * @param appVersion  the SIARD Suite build version
 * @param javaVersion the version of the Java runtime the agent runs on
 * @param jdbcDrivers class names of the JDBC drivers registered in the agent
 */
public record SystemInfo(
        @Schema(required = true) String appVersion,
        @Schema(required = true) String javaVersion,
        @Schema(required = true) List<String> jdbcDrivers) {
}
