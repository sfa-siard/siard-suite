package ch.admin.bar.siardsuite.webcore.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Turns every exception from a REST resource into problem details. Unexpected exceptions are
 * logged with their details here and answered with a generic 500 that reveals nothing internal;
 * only a {@link ProblemException} carries a {@code detail} to the client.
 */
@Provider
public class ErrorResponseMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorResponseMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof ProblemException problemException) {
            return problem(ProblemDetail.of(problemException.status(), problemException.detail()));
        }
        if (exception instanceof WebApplicationException webException) {
            return problem(ProblemDetail.of(webException.getResponse().getStatus(), null));
        }
        LOG.error("Unexpected error while handling a request", exception);
        return problem(ProblemDetail.of(500, null));
    }

    private static Response problem(ProblemDetail problem) {
        return Response.status(problem.status()).type(ProblemDetail.MEDIA_TYPE).entity(problem).build();
    }
}
