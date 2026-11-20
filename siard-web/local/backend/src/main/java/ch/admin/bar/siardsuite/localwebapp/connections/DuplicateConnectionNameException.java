package ch.admin.bar.siardsuite.localwebapp.connections;

/** A connection profile could not be written because its name is already taken. */
public class DuplicateConnectionNameException extends RuntimeException {

    public DuplicateConnectionNameException(String name, Throwable cause) {
        super("A connection named '" + name + "' already exists", cause);
    }
}
