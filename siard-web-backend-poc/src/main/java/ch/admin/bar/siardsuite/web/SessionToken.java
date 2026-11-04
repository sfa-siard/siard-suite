package ch.admin.bar.siardsuite.web;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.ConfigProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Holds the per-launch session token provided by {@link AgentConfigSource};
 * generates a random token if the configuration value is absent.
 */
@ApplicationScoped
public class SessionToken {

    private final String token;

    public SessionToken() {
        this.token = ConfigProvider.getConfig()
                .getOptionalValue(AgentConfigSource.TOKEN_PROPERTY, String.class)
                .orElseGet(() -> UUID.randomUUID().toString());
    }

    public String get() {
        return token;
    }

    public boolean matches(String candidate) {
        return candidate != null
                && MessageDigest.isEqual(
                        token.getBytes(StandardCharsets.UTF_8),
                        candidate.getBytes(StandardCharsets.UTF_8));
    }
}
