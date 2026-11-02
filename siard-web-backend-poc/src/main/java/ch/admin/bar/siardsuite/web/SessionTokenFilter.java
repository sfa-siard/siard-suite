package ch.admin.bar.siardsuite.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * Requires the per-launch session token on every {@code /api/*} request,
 * presented either as an {@code Authorization: Bearer} header or as a
 * {@code token} query parameter. Static resources and non-API endpoints are
 * not filtered.
 */
@Provider
public class SessionTokenFilter implements ContainerRequestFilter {

    private static final String API_PREFIX = "/api/";
    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    SessionToken sessionToken;

    @Override
    public void filter(ContainerRequestContext context) {
        String path = context.getUriInfo().getRequestUri().getPath();
        if (path == null || !path.startsWith(API_PREFIX)) {
            return;
        }
        // cheap DNS-rebinding defense: only requests addressed to the loopback
        // interface are legitimate for this local agent
        String host = context.getHeaderString("Host");
        if (host == null || !(host.startsWith("127.0.0.1:") || host.startsWith("localhost"))) {
            context.abortWith(Response.status(Response.Status.FORBIDDEN).build());
            return;
        }
        String token = bearerToken(context);
        if (token == null) {
            token = context.getUriInfo().getQueryParameters().getFirst("token");
        }
        if (!sessionToken.matches(token)) {
            context.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private static String bearerToken(ContainerRequestContext context) {
        String header = context.getHeaderString("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
