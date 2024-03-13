package ch.admin.bar.siardsuite.model;

import lombok.ToString;

import java.io.PrintWriter;
import java.io.StringWriter;

@ToString(onlyExplicitlyIncluded = true)
public class Failure {

    private final StringWriter stringWriter = new StringWriter();
    private final PrintWriter printWriter = new PrintWriter(stringWriter);

    @ToString.Include
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
