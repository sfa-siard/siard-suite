package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;

import java.io.File;
import java.net.URI;
import java.time.LocalDate;

public interface SiardArchiveMetaDataVisitor {

    void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
               String databaseUsername, String databaseDescription, String databseOwner, String databaseCreationDate,
               LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive, URI lobFolder,
               boolean viewsAsTables);

    void visit(SiardArchiveMetaData metaData);
}
