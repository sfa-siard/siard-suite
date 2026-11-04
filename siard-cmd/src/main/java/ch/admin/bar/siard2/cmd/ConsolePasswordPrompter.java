package ch.admin.bar.siard2.cmd;

import java.io.Console;

/**
 * Prompts for the database password on the system console without echoing input.
 * Returns {@code null} when no console is available.
 */
public class ConsolePasswordPrompter implements PasswordPrompter {

    @Override
    public char[] prompt() {
        Console console = System.console();
        if (console == null) return null;
        return console.readPassword("Database password: ");
    }
}
