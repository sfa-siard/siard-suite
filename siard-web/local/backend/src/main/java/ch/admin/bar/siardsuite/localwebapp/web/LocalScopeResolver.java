package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.webcore.archive.ArchiveId;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveScope;
import ch.admin.bar.siardsuite.webcore.archive.ScopeResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;

import java.util.Optional;

/**
 * One fixed archive scope per agent instance (design D1 of add-archive-browsing-core). Archive
 * requests only reach JAX-RS after {@link RequestFilters} accepted the session, so every request
 * the core asks about belongs to this instance's user.
 */
@ApplicationScoped
public class LocalScopeResolver implements ScopeResolver {

    private final ArchiveScope scope = new ArchiveScope(ArchiveId.random().value());

    /** The scope under which the agent registers archives. */
    public ArchiveScope scope() {
        return scope;
    }

    @Override
    public Optional<ArchiveScope> resolve(ContainerRequestContext request) {
        return Optional.of(scope);
    }
}
