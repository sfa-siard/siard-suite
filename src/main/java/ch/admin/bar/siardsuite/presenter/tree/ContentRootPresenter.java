package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.database.*;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;


/**
 * Represents the content root of the tree view of a SIARD Archive displaying general information
 * of the database and siard archive, e.g. SIARD Format Version, Database Name and product and other general informations.
 */
public class ContentRootPresenter extends TreePresenter implements SiardArchiveVisitor, SiardArchiveMetaDataVisitor {

    @FXML
    public VBox container;

    @FXML
    private Label siardFormatVersion;
    @FXML
    private Label databaseName;
    @FXML
    public Label databaseProduct;
    @FXML
    public Label jdbcConnectionUrl;
    @FXML
    public Label username;
    @FXML
    public Label description;
    @FXML
    public Label owner;
    @FXML
    public Label dataOriginTimeSpan;
    @FXML
    public Label archiveData;
    @FXML
    public Label archivedBy;
    @FXML
    public Label archiverContact;

    @Override
    public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        initLabels();

        this.model.provideDatabaseArchiveMetaDataProperties(this);
        this.model.provideDatabaseArchiveProperties(this);
    }

    private void initLabels() {
        for (Node child : this.container.getChildren()) {
            if (child.getId().contains("label")) {
                ((Label) child).textProperty().bind(I18n.createStringBinding("tableContainer." + child.getId()));
            }
        }
    }

    @Override
    public void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas, List<User> users,
                      List<Privilige> priviliges) {
    }

    @Override
    public void visitSchema(String schemaName, String schemaDescription, List<DatabaseTable> tables,
                            List<DatabaseView> views, List<DatabaseType> types, List<DatabaseRoutine> routines) {
    }

    @Override
    public void visit(String tableName, String numberOfRows, List<DatabaseColumn> columns, List<DatabaseRow> rows) {
    }

    @Override
    public void visit(String columnName) {
    }

    @Override
    public void visit(SiardArchive archive) {
    }

    @Override
    public void visit(String siardFormatVersion, String databaseName, String databaseProduct,
                      String databaseConnectionURL,
                      String databaseUsername, String databaseDescription, String databaseOwner,
                      String databaseCreationDate,
                      LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive, URI lobFolder) {
        this.siardFormatVersion.setText(siardFormatVersion);
        this.databaseName.setText(databaseName);
        this.databaseProduct.setText(databaseProduct);
        jdbcConnectionUrl.setText(databaseConnectionURL);
        username.setText(databaseUsername);
        description.setText(databaseDescription);
        owner.setText(databaseOwner);
        dataOriginTimeSpan.setText(databaseCreationDate);
        archiveData.textProperty()
                   .bind(Bindings.createStringBinding(() -> I18n.getLocaleDate(archivingDate),
                                                      I18n.localeProperty()));
        archivedBy.setText(archiverName);
        this.archiverContact.setText(archiverContact);
    }

    @Override
    public void visit(SiardArchiveMetaData metaData) {
    }

}
