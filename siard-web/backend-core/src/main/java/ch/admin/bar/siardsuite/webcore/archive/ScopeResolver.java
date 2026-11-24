package ch.admin.bar.siardsuite.webcore.archive;

import jakarta.ws.rs.container.ContainerRequestContext;

import java.util.Optional;

/**
 * Determines the scope of an archive request. Every app built on the core must provide exactly one
 * implementation; the core has no default, so a missing one fails the build instead of serving
 * archives to everyone (design D1). An empty result is answered with 401.
 */
public interface ScopeResolver {

    Optional<ArchiveScope> resolve(ContainerRequestContext request);
}
