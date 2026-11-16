package ch.admin.bar.siardsuite.localwebapp.agent;

import io.quarkus.runtime.LaunchMode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Launch tokens and the session secret of one agent instance (design D4, D8).
 * <p>
 * Outside {@link LaunchMode#DEVELOPMENT}, tokens and the secret are random, a token is accepted
 * once and expires {@link #TOKEN_LIFETIME} after it was issued. In development mode the
 * configured fixed token and secret are used, and the token is reusable and does not expire.
 */
public class LaunchCredentials {

    public static final Duration TOKEN_LIFETIME = Duration.ofMinutes(2);

    private static final SecureRandom RANDOM = new SecureRandom();

    private final Clock clock;
    private final String sessionSecret;
    private final String fixedToken;
    private final Map<String, Instant> issuedTokens = new ConcurrentHashMap<>();

    public LaunchCredentials(LaunchMode mode, Optional<String> devToken, Optional<String> devSessionSecret, Clock clock) {
        this.clock = clock;
        boolean development = mode == LaunchMode.DEVELOPMENT && devToken.isPresent() && devSessionSecret.isPresent();
        this.fixedToken = development ? devToken.get() : null;
        this.sessionSecret = development ? devSessionSecret.get() : randomValue();
    }

    /**
     * Issues a token for a launch URL: the fixed one in development mode, otherwise a new random one.
     */
    public String issueToken() {
        if (fixedToken != null) {
            return fixedToken;
        }
        String token = randomValue();
        issuedTokens.put(token, clock.instant());
        return token;
    }

    /**
     * Returns whether the token may be exchanged for a session; a random token is invalidated by this call.
     */
    public boolean redeem(String token) {
        if (token == null) {
            return false;
        }
        if (fixedToken != null) {
            return constantTimeEquals(fixedToken, token);
        }
        String matched = issuedTokens.keySet().stream()
                .filter(issued -> constantTimeEquals(issued, token))
                .findFirst()
                .orElse(null);
        if (matched == null) {
            return false;
        }
        Instant issuedAt = issuedTokens.remove(matched);
        return issuedAt != null && !clock.instant().isAfter(issuedAt.plus(TOKEN_LIFETIME));
    }

    public String sessionSecret() {
        return sessionSecret;
    }

    public boolean isSessionSecret(String candidate) {
        return candidate != null && constantTimeEquals(sessionSecret, candidate);
    }

    private static boolean constantTimeEquals(String expected, String candidate) {
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), candidate.getBytes(StandardCharsets.UTF_8));
    }

    private static String randomValue() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
