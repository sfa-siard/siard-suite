package ch.admin.bar.siardsuite.web;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.net.URI;
import java.sql.DriverManager;

/**
 * Logs the agent URL with the session token, enumerates the JDBC drivers
 * available in the package, and opens the default browser when possible.
 */
@ApplicationScoped
public class AgentStartupLogger {

    private static final Logger LOG = LoggerFactory.getLogger(AgentStartupLogger.class);

    @Inject
    SessionToken sessionToken;

    @ConfigProperty(name = "quarkus.http.port")
    int port;

    void onStart(@Observes StartupEvent event) {
        String url = "http://127.0.0.1:" + effectivePort() + "/?token=" + sessionToken.get();
        LOG.info("SIARD web agent listening at {}", url);
        logJdbcDrivers();
        openBrowser(url);
    }

    // an explicitly configured port (e.g. dev mode 8080) wins; otherwise the
    // port pre-allocated by AgentConfigSource is the one the server bound to
    private int effectivePort() {
        if (port != 0) {
            return port;
        }
        return ConfigProvider.getConfig()
                .getOptionalValue(AgentConfigSource.PORT_PROPERTY, Integer.class)
                .orElse(port);
    }

    private void logJdbcDrivers() {
        DriverManager.getDrivers()
                .asIterator()
                .forEachRemaining(driver ->
                        LOG.info("JDBC driver registered: {}", driver.getClass().getName()));
    }

    private void openBrowser(String url) {
        if (!GraphicsEnvironment.isHeadless()
                && Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(URI.create(url));
                return;
            } catch (Exception e) {
                LOG.warn("Could not open the default browser: {}", e.getMessage());
            }
        }
        LOG.info("No browser opened automatically. Navigate to: {}", url);
    }
}
