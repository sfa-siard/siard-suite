package ch.admin.bar.siardsuite.localwebapp;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Spec scenarios that need the packaged app in normal launch mode ("Loopback-only server on a
 * free port"; the two packaged scenarios of "Fixed development credentials only in development mode").
 */
class PackagedAgentIT {

    @Test
    void agentStartsAndLogsItsLaunchUrl() throws Exception {
        try (PackagedAgent agent = PackagedAgent.start()) {
            assertTrue(agent.launchUrl().startsWith("http://127.0.0.1:"));
            assertTrue(agent.port() >= 1024 && agent.port() <= 65535, "port " + agent.port());
            assertFalse(agent.token().isEmpty());
            assertEquals(200, agent.get("/q/health").statusCode());
        }
    }

    @Test
    void notReachableFromOtherNetworkInterfaces() throws Exception {
        Optional<InetAddress> otherAddress = nonLoopbackIpv4Address();
        assumeTrue(otherAddress.isPresent(), "no non-loopback IPv4 address on this machine");

        try (PackagedAgent agent = PackagedAgent.start()) {
            // only "connection refused" proves the loopback binding; a timeout or a firewall
            // block would fail for other reasons and must not count as success
            ConnectException refused = assertThrows(ConnectException.class, () -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(otherAddress.get(), agent.port()), 2000);
                }
            });
            assertTrue(refused.getMessage() != null && refused.getMessage().contains("Connection refused"),
                    "expected 'Connection refused' but got: " + refused.getMessage());
        }
    }

    @Test
    void packagedAgentWithDevProfileOverrideRejectsDevToken() throws Exception {
        try (PackagedAgent agent = PackagedAgent.start("-Dquarkus.profile=dev", "-Dquarkus.http.port=0")) {
            HttpResponse<String> response = agent.get("/launch?token=" + configuredDevToken());
            assertEquals("/launch-failed", response.headers().firstValue("Location").orElse(null));
            assertTrue(response.headers().firstValue("Set-Cookie").isEmpty());
        }
    }

    @Test
    void tokenCannotBeSetByConfiguration() throws Exception {
        String configured = "configured-token";
        try (PackagedAgent agent = PackagedAgent.start(
                "-Dsiard.agent.token=" + configured,
                "-Dsiard.agent.dev-token=" + configured,
                "-Dsiard.agent.dev-session-secret=configured-secret")) {
            HttpResponse<String> response = agent.get("/launch?token=" + configured);
            assertEquals("/launch-failed", response.headers().firstValue("Location").orElse(null));
            assertTrue(response.headers().firstValue("Set-Cookie").isEmpty());
            assertNotEquals(configured, agent.token());
        }
    }

    @Test
    void lastWindowClosed() throws Exception {
        Duration grace = Duration.ofSeconds(2);
        try (PackagedAgent agent = PackagedAgent.start("-Dsiard.agent.idle-exit-grace=" + grace.toMillis() + "ms")) {
            agent.openWindow(agent.sessionCookie()).close();
            assertTrue(agent.awaitExit(grace.plusSeconds(5)), "agent still running after the grace period plus 5 seconds");
        }
    }

    // read from the shipped config so the test keeps up if the dev token value changes
    private static String configuredDevToken() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = PackagedAgentIT.class.getResourceAsStream("/application.properties")) {
            assertNotNull(in, "application.properties not on the test classpath");
            properties.load(in);
        }
        String token = properties.getProperty("%dev.siard.agent.dev-token");
        assertNotNull(token, "%dev.siard.agent.dev-token missing in application.properties");
        return token;
    }

    private static Optional<InetAddress> nonLoopbackIpv4Address() throws IOException {
        for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                continue;
            }
            for (InetAddress address : Collections.list(networkInterface.getInetAddresses())) {
                if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                    return Optional.of(address);
                }
            }
        }
        return Optional.empty();
    }
}
