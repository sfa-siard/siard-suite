package ch.admin.bar.siardsuite.localwebapp.agent;

import io.quarkus.vertx.http.HttpServer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The port the HTTP server actually listens on (design D3); with {@code quarkus.http.port=0}
 * only the server knows it.
 */
@ApplicationScoped
public class AgentPort {

    @Inject
    HttpServer httpServer;

    public int get() {
        return httpServer.getPort();
    }
}
