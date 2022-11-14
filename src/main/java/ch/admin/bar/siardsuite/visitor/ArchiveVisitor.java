package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;

public interface ArchiveVisitor {
    void visit(Archive archive);
    void visit(MetaData metaData);
}
