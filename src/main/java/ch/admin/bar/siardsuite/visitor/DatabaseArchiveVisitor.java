package ch.admin.bar.siardsuite.visitor;

import ch.admin.bar.siardsuite.model.database.DatabaseArchive;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableView;

import java.util.List;

public interface DatabaseArchiveVisitor {

    void visit(String archiveName, boolean onlyMetaData);

    void visit(DatabaseArchive archive);

}
