package ch.admin.bar.siardsuite.webcore.archive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;

import java.util.Optional;

/**
 * Test scope resolver: the scope is the value of the {@value #HEADER} header; without it there is
 * no scope.
 */
@ApplicationScoped
public class TestScopeResolver implements ScopeResolver {

    public static final String HEADER = "X-Test-Scope";

    @Override
    public Optional<ArchiveScope> resolve(ContainerRequestContext request) {
        return Optional.ofNullable(request.getHeaderString(HEADER)).map(ArchiveScope::new);
    }
}
