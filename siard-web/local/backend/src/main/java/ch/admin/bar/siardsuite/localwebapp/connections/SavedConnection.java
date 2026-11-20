package ch.admin.bar.siardsuite.localwebapp.connections;

import java.time.Instant;

/**
 * A saved connection profile as exposed by the API. It never carries a password: passwords are
 * not stored, transmitted or logged (spec "Passwords are never stored or transmitted").
 *
 * @param id         stable identifier, assigned by the store
 * @param name       unique display name chosen by the user
 * @param dbmsId     identifier of the {@code Dbms} the profile connects to
 * @param host       server host
 * @param port       server port
 * @param dbName     database name
 * @param user       database user
 * @param options    free-form JDBC options, may be null
 * @param file       database file, reserved for the future file-based flow, may be null
 * @param createdAt  when the profile was saved
 * @param updatedAt  when the profile was last changed
 */
public record SavedConnection(
        long id,
        String name,
        String dbmsId,
        String host,
        String port,
        String dbName,
        String user,
        String options,
        String file,
        Instant createdAt,
        Instant updatedAt) {
}
