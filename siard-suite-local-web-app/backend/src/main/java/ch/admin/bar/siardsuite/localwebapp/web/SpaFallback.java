package ch.admin.bar.siardsuite.localwebapp.web;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * Serves {@code index.html} for client-side routes such as {@code /about}, so a reload or deep
 * link works (design D12). It runs after the request filters but before static files and REST,
 * which answers every unmatched path with a 404 itself (a problem response under {@code /api/}).
 * Only GET requests to paths without an extension in the last segment are rerouted, so static
 * files are never shadowed and a mistyped {@code .js} stays a 404; the agent's own paths
 * ({@code /api}, {@code /q}, {@code /launch}) never fall back.
 */
@ApplicationScoped
public class SpaFallback {

    void register(@Observes Router router) {
        router.get().order(Integer.MIN_VALUE + 2).handler(this::fallBackToIndex);
    }

    private void fallBackToIndex(RoutingContext context) {
        if (isClientRoute(context.normalizedPath())) {
            context.reroute("/");
        } else {
            context.next();
        }
    }

    static boolean isClientRoute(String path) {
        if (path.equals("/") || isUnder(path, "/api") || isUnder(path, "/q") || isUnder(path, "/launch")) {
            return false;
        }
        String lastSegment = path.substring(path.lastIndexOf('/') + 1);
        return !lastSegment.contains(".");
    }

    private static boolean isUnder(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }
}
