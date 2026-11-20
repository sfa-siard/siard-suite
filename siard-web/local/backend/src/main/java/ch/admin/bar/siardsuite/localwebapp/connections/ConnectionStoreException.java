package ch.admin.bar.siardsuite.localwebapp.connections;

/** The connection store could not be opened or queried; surfaces as a problem-details 500. */
public class ConnectionStoreException extends RuntimeException {

    public ConnectionStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
