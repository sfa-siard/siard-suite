package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;

public interface SiardArchiveMetaDataVisitor {


    void visit(SiardArchiveMetaData metaData);
}
