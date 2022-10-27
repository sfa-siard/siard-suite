package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siardsuite.model.database.DatabaseArchiveMetaData;

import java.io.File;
import java.time.LocalDate;

public interface DatabaseArchiveMetaDataVisitor {

    void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
               String databaseUsername, String databaseDescription, String databseOwner, String databaseCreationDate,
               LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive);

    void visit(DatabaseArchiveMetaData metaData);

}
