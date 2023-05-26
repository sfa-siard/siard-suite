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
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URI;
import java.time.LocalDate;

import static ch.admin.bar.siardsuite.util.StringUtils.emptyApiNull;


/**
 * Show details of the SIARD archive.
 */
public class ArchiveDetailsPresenter extends DetailsPresenter implements SiardArchiveMetaDataVisitor {

    @FXML
    private VBox container;
    @FXML
    private Label labelFormat;
    @FXML
    private Label labelDb;
    @FXML
    private Label labelProduct;
    @FXML
    private Label labelConnection;
    @FXML
    private Label labelUsername;
    @FXML
    private Label labelDesc;
    @FXML
    private Label labelOwner;
    @FXML
    private Label labelCreationDate;
    @FXML
    private Label labelArchiveDate;
    @FXML
    private Label labelArchiveUser;
    @FXML
    private Label labelContactArchiveUser;
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

        bindLabels();

        // TODO: align pattern with other SubClasses of DetailsPresenter
        this.model.provideDatabaseArchiveMetaDataProperties(this);
    }

    @Override
    protected void bindLabels() {
        I18n.bind(labelFormat, "archiveDetails.labelFormat");
        I18n.bind(labelDb, "archiveDetails.labelDb");
        I18n.bind(labelProduct, "archiveDetails.labelProduct");
        I18n.bind(labelConnection, "archiveDetails.labelConnection");
        I18n.bind(labelUsername, "archiveDetails.labelUsername");
        I18n.bind(labelDesc, "archiveDetails.labelDesc");
        I18n.bind(labelOwner, "archiveDetails.labelOwner");
        I18n.bind(labelCreationDate, "archiveDetails.labelCreationDate");
        I18n.bind(labelArchiveDate, "archiveDetails.labelArchiveDate");
        I18n.bind(labelArchiveUser, "archiveDetails.labelArchiveUser");
        I18n.bind(labelContactArchiveUser, "archiveDetails.labelContactArchiveUser");

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
