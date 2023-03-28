package ch.admin.bar.siardsuite;

// representation of the four major workflows
public enum Workflow {
    ARCHIVE, // archive DB to SIARD File
    OPEN, // Open a SIARD file
    EXPORT, // Export Data from a SIARD file to Excel?
    UPLOAD // Restore DB from SIARD file
}
