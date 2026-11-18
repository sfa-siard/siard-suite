package ch.admin.bar.siardsuite.webcore.archive;

/**
 * Who may reach an archive. Its meaning is up to the app (one agent instance locally, a user or
 * group remotely); the core only compares scopes for equality (design D1).
 */
public record ArchiveScope(String value) {
}
