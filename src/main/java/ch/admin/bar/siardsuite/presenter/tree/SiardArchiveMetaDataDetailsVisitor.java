package ch.admin.bar.siardsuite.presenter.tree;

import java.io.File;
import java.net.URI;
import java.time.LocalDate;

public interface SiardArchiveMetaDataDetailsVisitor {
    void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
               String databaseUsername, String databaseDescription, String databseOwner, String databaseCreationDate,
               LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive, URI lobFolder,
               boolean viewsAsTables);
}
