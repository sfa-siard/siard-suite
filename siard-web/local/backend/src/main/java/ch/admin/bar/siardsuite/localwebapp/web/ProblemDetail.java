package ch.admin.bar.siardsuite.localwebapp.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.ws.rs.core.Response;

/**
 * RFC 9457 problem details, the body of every error response (design D10).
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
