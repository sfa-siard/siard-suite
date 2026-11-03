package ch.admin.bar.siard2.cmd;

/**
 * Supplies a database password interactively.
 * Implementations return {@code null} when no console is available for prompting.
 */
public interface PasswordPrompter {

    char[] prompt();
}
