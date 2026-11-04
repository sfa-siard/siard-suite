package ch.admin.bar.siardsuite.web;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Provides the per-launch agent parameters (a pre-allocated free port and a
 * session token) as configuration values. Registered via ServiceLoader so it
 * participates in config resolution before the HTTP server binds — regardless
 * of how the application was started (packaged launcher, {@code java -jar},
 * dev mode, tests).
 */
public class AgentConfigSource implements ConfigSource {

    public static final String PORT_PROPERTY = "siard.agent.port";
    public static final String TOKEN_PROPERTY = "siard.agent.token";

    // static on purpose: the config source may be instantiated more than once
    // per JVM (augmentation vs. runtime) and every instance must expose the
    // same launch port and token
    private static final Map<String, String> VALUES = Map.of(
            PORT_PROPERTY, String.valueOf(freePort()),
            TOKEN_PROPERTY, UUID.randomUUID().toString());

    private final Map<String, String> values = VALUES;

    @Override
    public String getValue(String propertyName) {
        return values.get(propertyName);
    }

    @Override
    public Set<String> getPropertyNames() {
        return values.keySet();
    }

    @Override
    public String getName() {
        return "siard-agent-launch";
    }

    private static int freePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("could not allocate a free port", e);
        }
    }
}
