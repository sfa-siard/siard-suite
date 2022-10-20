package ch.admin.bar.siardsuite.presenter.archive;

public interface ArchiveMetaDataVisitor {

    void visit(String description, String owner, String timeOfOrigin, String archiverName, String archiverContact);
}
