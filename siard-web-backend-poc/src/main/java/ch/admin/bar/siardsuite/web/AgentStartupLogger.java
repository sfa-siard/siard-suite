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
import java.io.IOException;
import java.net.URI;
import java.sql.DriverManager;
import java.util.List;

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

    // Chromium-family executables that support --app=<url> (dedicated window,
    // no URL bar). On Windows they resolve through registered App Paths.
    private static final List<String> APP_MODE_BROWSERS = List.of(
            "msedge", "microsoft-edge", "chrome", "google-chrome",
            "google-chrome-stable", "chromium", "chromium-browser", "brave", "brave-browser");

    private void openBrowser(String url) {
        if (tryAppModeWindow(url)) {
            return;
        }
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

    private boolean tryAppModeWindow(String url) {
        boolean windows = System.getProperty("os.name", "").toLowerCase().contains("win");
        for (String browser : APP_MODE_BROWSERS) {
            try {
                ProcessBuilder pb = windows
                        ? new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", browser, "--app=" + url)
                        : new ProcessBuilder(browser, "--app=" + url);
                pb.start();
                LOG.info("Opened {} in application-window mode", browser);
                return true;
            } catch (IOException e) {
                // not installed — try the next candidate
            }
        }
        return false;
    }
}
