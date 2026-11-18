package ch.admin.bar.siardsuite.webcore.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.ws.rs.core.Response;

/**
 * RFC 9457 problem details, the body of every error response (design D10 of the archived change add-local-web-app-setup).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetail(String type, String title, int status, String detail) {

    public static final String MEDIA_TYPE = "application/problem+json";

    public static ProblemDetail of(int status, String detail) {
        Response.Status known = Response.Status.fromStatusCode(status);
        String title = known != null ? known.getReasonPhrase() : "HTTP " + status;
        return new ProblemDetail("about:blank", title, status, detail);
    }
}
