package ch.admin.bar.siard2.cmd;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Resolves the database password in precedence order:
 * {@code --password=<value>} > interactive prompt on bare {@code --password} > {@code SIARD_PASSWORD} env var.
 * Prints a diagnostic and returns {@code null} when no password could be resolved (usage error).
 */
final class PasswordResolver {

    private PasswordResolver() {
    }

    static String resolve(String passwordOption,
                          boolean passwordRequested,
                          PasswordPrompter prompter,
                          Function<String, String> env,
                          PrintWriter err) {
        if (passwordOption != null && !passwordOption.isEmpty()) {
            err.println("Warning: --password=<value> exposes the password via process listing; prefer --password to prompt or set SIARD_PASSWORD.");
            return passwordOption;
        }

        if (passwordRequested) {

            char[] prompted = prompter.prompt();
            if (prompted != null) {
                String result = new String(prompted);
                Arrays.fill(prompted, '\0');
                return result;
            }

            err.println("No console available for password prompt; use --password=<value> or set SIARD_PASSWORD.");
            return null;
        }

        String envPassword = env.apply("SIARD_PASSWORD");
        if (envPassword != null) return envPassword;

        err.println("Database password must be given; use --password to prompt or set SIARD_PASSWORD.");
        return null;
    }
}
