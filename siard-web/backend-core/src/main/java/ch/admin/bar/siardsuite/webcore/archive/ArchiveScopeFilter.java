package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.Optional;

/**
 * Resolves the scope of every {@code /api/archives/**} request through the app's
 * {@link ScopeResolver} and answers 401 before any archive is looked up when there is none.
 */
@Provider
public class ArchiveScopeFilter implements ContainerRequestFilter {

    private static final String SCOPE_PROPERTY = ArchiveScope.class.getName();

    @Inject
    ScopeResolver resolver;

    @Override
    public void filter(ContainerRequestContext request) {
        String path = request.getUriInfo().getPath();
        if (!path.equals("/api/archives") && !path.startsWith("/api/archives/")) {
            return;
        }
        Optional<ArchiveScope> scope = resolver.resolve(request);
        if (scope.isEmpty()) {
            ProblemDetail problem = ProblemDetail.of(401, null);
            request.abortWith(Response.status(401).type(ProblemDetail.MEDIA_TYPE).entity(problem).build());
            return;
        }
        request.setProperty(SCOPE_PROPERTY, scope.get());
    }

    /** The scope this filter resolved for the request. */
    static ArchiveScope scopeOf(ContainerRequestContext request) {
        return (ArchiveScope) request.getProperty(SCOPE_PROPERTY);
    }
}
