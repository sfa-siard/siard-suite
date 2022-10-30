package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siardsuite.model.database.SiardArchive;

public interface DatabaseArchiveVisitor {

    void visit(String archiveName, boolean onlyMetaData);

    void visit(SiardArchive archive);

}
