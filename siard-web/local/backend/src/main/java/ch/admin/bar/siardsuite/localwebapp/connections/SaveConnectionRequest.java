package ch.admin.bar.siardsuite.localwebapp.connections;

/**
 * Request body of {@code POST /api/connections}. There is deliberately no password field:
 * passwords are never transmitted to the agent (spec "Passwords are never stored or
 * transmitted").
 */
public record SaveConnectionRequest(
        String name,
        String dbmsId,
        String host,
        String port,
        String dbName,
        String user,
        String options) {
}
