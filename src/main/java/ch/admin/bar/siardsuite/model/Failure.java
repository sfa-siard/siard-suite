package ch.admin.bar.siardsuite.model;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Failure {

    private final StringWriter stringWriter = new StringWriter();
    private final PrintWriter printWriter = new PrintWriter(stringWriter);

    private final Throwable throwable;

    public Failure(Throwable throwable) {
        this.throwable = throwable;
    }

    public String message() {
        return throwable.getLocalizedMessage();
    }

    public String stacktrace() {
        this.throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
