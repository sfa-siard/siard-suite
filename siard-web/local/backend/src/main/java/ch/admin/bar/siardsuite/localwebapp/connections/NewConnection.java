package ch.admin.bar.siardsuite.localwebapp.connections;

/**
 * The fields of a connection profile before it is saved; the store assigns id and timestamps.
 * No password field exists on purpose.
 */
public record NewConnection(
        String name,
        String dbmsId,
        String host,
        String port,
        String dbName,
        String user,
        String options,
        String file) {
}
