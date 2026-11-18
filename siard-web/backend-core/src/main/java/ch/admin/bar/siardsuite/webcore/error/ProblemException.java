package ch.admin.bar.siardsuite.webcore.error;

/**
 * A client error raised deliberately: its {@code detail} is written to the problem details as it
 * is, so it must be meant for the client and must never carry an exception message (design D9).
 */
public class ProblemException extends RuntimeException {

    private final int status;
    private final String detail;

    public ProblemException(int status, String detail) {
        super(detail, null, false, false);
        this.status = status;
        this.detail = detail;
    }

    public int status() {
        return status;
    }

    public String detail() {
        return detail;
    }
}
