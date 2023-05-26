package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URI;
import java.time.LocalDate;

import static ch.admin.bar.siardsuite.util.StringUtils.emptyApiNull;


/**
 * Represents the content root of the tree view of a SIARD Archive displaying general information
 * of the database and siard archive, e.g. SIARD Format Version, Database Name and product and other general informations.
 */
public class ContentRootPresenter extends DetailsPresenter implements SiardArchiveMetaDataVisitor {

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

        initializeLabels();

        this.model.provideDatabaseArchiveMetaDataProperties(this);
    }

    @Override
    protected void bindLabels() {

    }

    // Dynamically initialize labels from ch/admin/bar/siardsuite/fxml/tree/content-root.fxml based on the id!
    private void initializeLabels() {
        this.container.getChildren().forEach(this::initLabel);
    }

    private void initLabel(Node node) {
        if (!node.getId().contains("label")) return;
        I18n.bind(((Label) node).textProperty(), "tableContainer." + node.getId());
    }

    @Override
    public void visit(String siardFormatVersion,
                      String databaseName,
                      String databaseProduct,
                      String databaseConnectionURL,
                      String databaseUsername,
                      String databaseDescription,
                      String databaseOwner,
                      String databaseCreationDate,
                      LocalDate archivingDate,
                      String archiverName,
                      String archiverContact,
                      File targetArchive,
                      URI lobFolder) {
        this.siardFormatVersion.setText(emptyApiNull(siardFormatVersion));
        this.databaseName.setText(emptyApiNull(databaseName));
        this.databaseProduct.setText(emptyApiNull(databaseProduct));
        this.jdbcConnectionUrl.setText(emptyApiNull(databaseConnectionURL));
        this.username.setText(emptyApiNull(databaseUsername));
        this.description.setText(emptyApiNull(databaseDescription));
        this.owner.setText(emptyApiNull(databaseOwner));
        this.dataOriginTimeSpan.setText(emptyApiNull(databaseCreationDate));
        this.archiveData.textProperty()
                        .bind(Bindings.createStringBinding(() -> I18n.getLocaleDate(archivingDate),
                                                           I18n.localeProperty()));
        this.archivedBy.setText(emptyApiNull(archiverName));
        this.archiverContact.setText(emptyApiNull(archiverContact));
    }

    @Override
    public void visit(SiardArchiveMetaData metaData) {
        // TODO: should not be in interface SiardArchiveMetaDataVisitor
    }
}
