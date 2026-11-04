package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.SiardCliTest.Result;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static ch.admin.bar.siard2.cmd.SiardCliTest.execute;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordResolutionTest {

    private static final String UNREACHABLE_PG_URL = "jdbc:postgresql://localhost:1/db";

    private Logger cmdLogger;
    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void attachLogAppender() {
        cmdLogger = (Logger) LoggerFactory.getLogger("ch.admin.bar");
        logAppender = new ListAppender<>();
        logAppender.start();
        cmdLogger.addAppender(logAppender);
    }

    @AfterEach
    void detachLogAppender() {
        cmdLogger.detachAppender(logAppender);
    }

    @Test
    void passwordNeverLeaked_atDebugLogLevel(@TempDir Path tempDir) {
        // given the package logger temporarily at DEBUG — the strictest leak surface
        Level previousLevel = cmdLogger.getLevel();
        cmdLogger.setLevel(Level.DEBUG);
        try {
            // when
            Result result = execute(() -> null,
                                    Map.<String, String>of()::get,
                                    "archive",
                                    "--jdbc-url=" + UNREACHABLE_PG_URL,
                                    "--user=u",
                                    "--password=dbgsecret",
                                    "--siard-file=" + tempDir.resolve("out.siard"));

            // then
            assertThat(result.out()).doesNotContain("dbgsecret");
            assertThat(result.err()).doesNotContain("dbgsecret");
            assertThat(logAppender.list).noneMatch(e -> e.getFormattedMessage().contains("dbgsecret"));
        } finally {
            cmdLogger.setLevel(previousLevel);
        }
    }

    @Test
    void passwordOnCommandLine_isUsedAndNeverLeaked(@TempDir Path tempDir) {
        // given
        PasswordPrompter failOnPrompt = () -> {
            throw new AssertionError("prompt must not be called when --password has a value");
        };

        // when
        Result result = execute(failOnPrompt,
                                Map.<String, String>of()::get,
                                "archive",
                                "--jdbc-url=" + UNREACHABLE_PG_URL,
                                "--user=u",
                                "--password=secret",
                                "--siard-file=" + tempDir.resolve("out.siard"));

        // then the credential was accepted and the run proceeded to an execution error, not a usage error
        assertThat(result.exitCode()).isEqualTo(1);
        assertThat(result.out()).doesNotContain("secret");
        assertThat(result.err()).doesNotContain("secret");
        assertThat(logAppender.list).noneMatch(event -> event.getFormattedMessage().contains("secret"));
    }

    @Test
    void passwordWithoutValue_promptsAndUsesEnteredValue(@TempDir Path tempDir) {
        // given
        AtomicBoolean promptCalled = new AtomicBoolean(false);
        PasswordPrompter fakePrompter = () -> {
            promptCalled.set(true);
            return "prompted".toCharArray();
        };

        // when
        Result result = execute(fakePrompter,
                                Map.<String, String>of()::get,
                                "archive",
                                "--jdbc-url=" + UNREACHABLE_PG_URL,
                                "--user=u",
                                "--password",
                                "--siard-file=" + tempDir.resolve("out.siard"));

        // then
        assertThat(promptCalled).isTrue();
        assertThat(result.exitCode()).isEqualTo(1);
    }

    @Test
    void passwordWithExplicitEmptyValue_isUsedWithoutPrompt(@TempDir Path tempDir) {
        // given
        PasswordPrompter failOnPrompt = () -> {
            throw new AssertionError("prompt must not be called when --password= is given");
        };

        // when
        Result result = execute(failOnPrompt,
                                Map.<String, String>of()::get,
                                "archive",
                                "--jdbc-url=" + UNREACHABLE_PG_URL,
                                "--user=u",
                                "--password=",
                                "--siard-file=" + tempDir.resolve("out.siard"));

        // then the empty password was accepted and the run proceeded to an execution error, not a usage error
        assertThat(result.exitCode()).isEqualTo(1);
    }

    @Test
    void passwordWithoutValueAndNoConsole_explainsAndExits2(@TempDir Path tempDir) {
        // given no console available
        PasswordPrompter noConsole = () -> null;

        // when
        Result result = execute(noConsole,
                                Map.<String, String>of()::get,
                                "archive",
                                "--jdbc-url=" + UNREACHABLE_PG_URL,
                                "--user=u",
                                "--password",
                                "--siard-file=" + tempDir.resolve("out.siard"));

        // then
        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.err()).contains("console");
    }

    @Test
    void passwordFromEnvironment_isUsedWhenOptionAbsent(@TempDir Path tempDir) {
        // when
        Result result = execute(() -> null,
                                Map.of("SIARD_PASSWORD", "envsecret")::get,
                                "archive",
                                "--jdbc-url=" + UNREACHABLE_PG_URL,
                                "--user=u",
                                "--siard-file=" + tempDir.resolve("out.siard"));

        // then
        assertThat(result.exitCode()).isEqualTo(1);
        assertThat(result.out()).doesNotContain("envsecret");
        assertThat(result.err()).doesNotContain("envsecret");
    }

    @Test
    void noPasswordAtAll_hintsAtPasswordOptionAndExits2(@TempDir Path tempDir) {
        // when
        Result result = execute(() -> null,
                                Map.<String, String>of()::get,
                                "archive",
                                "--jdbc-url=" + UNREACHABLE_PG_URL,
                                "--user=u",
                                "--siard-file=" + tempDir.resolve("out.siard"));

        // then
        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.err()).contains("--password");
    }
}
