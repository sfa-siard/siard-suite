package ch.admin.bar.siardsuite.localwebapp.agent;

import io.quarkus.vertx.http.HttpServerStart;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs the launch URL once the server listens. The token in it is single-use and short-lived,
 * so the logged line is worthless once the window has opened.
 */
@ApplicationScoped
public class LaunchUrlLogger {

    private static final Logger LOG = LoggerFactory.getLogger(LaunchUrlLogger.class);

    @Inject
    AgentPort port;

    @Inject
    LaunchCredentials credentials;

    void onStart(@ObservesAsync HttpServerStart start) {
        String url = "http://127.0.0.1:" + port.get() + "/launch?token=" + credentials.issueToken();
        LOG.info("SIARD Suite agent launch URL: {}", url);
    }
}
