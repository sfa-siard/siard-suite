package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.*;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.ArchiveVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.enums.StepperToggleState;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.*;
import static ch.admin.bar.siardsuite.model.View.START;
import static ch.admin.bar.siardsuite.util.SiardEvent.DATABASE_DOWNLOADED;

public class ArchiveDownloadPresenter extends StepperPresenter implements SiardArchiveMetaDataVisitor, ArchiveVisitor {

  @FXML
  public Label title;
  @FXML
  public Label resultTitle;
  @FXML
  public ImageView loader;
  @FXML
  public Label recordsLoaded;
  @FXML
  public Label archivePath;
  @FXML
  public Label pathTitle;
  @FXML
  public Label openLink;
  @FXML
  public VBox fileSystemBox;
  @FXML
  public Label subtitle1;
  @FXML
  public ScrollPane resultBox;
  @FXML
  public VBox scrollBox;
  @FXML
  private StepperButtonBox buttonsBox;

  private Spinner loadingSpinner;
  private File targetArchive;
  private Archive archive;

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    this.controller = controller;
    this.model = model;
    this.stage = stage;

    this.loader.setImage(Icon.loading);
    loadingSpinner = new Spinner(this.loader);
    this.bindTexts();
  }

  @Override
  public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
    this.init(controller, model, stage);
    this.buttonsBox = new StepperButtonBox().make(CANCEL);
    addButtons(stepper);
    setListeners(stepper);
  }

  private void setListeners(MFXStepper stepper) {
    stepper.addEventHandler(SiardEvent.ARCHIVE_METADATA_UPDATED, downloadAndArchiveDatabase(stepper));

    if (this.buttonsBox.cancel() != null) {
      if (this.resultTitle.isVisible()) {
        this.buttonsBox.cancel().setOnAction((event) -> {
          stepper.previous();
        });
      } else {
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
      }
    }
    if (this.buttonsBox.next() != null) {
      this.buttonsBox.next().setOnAction((event) -> stage.navigate(START));
    }
  }

  private void bindTexts() {
    I18n.bind(title.textProperty(), "archiveDownload.view.title.inProgress");
    I18n.bind(recordsLoaded.textProperty(), "archiveDownload.view.message.inProgress", 6666);
    I18n.bind(pathTitle.textProperty(), "archiveDownload.view.pathTitle");
    I18n.bind(openLink.textProperty(), "archiveDownload.view.openLink");
  }

  private EventHandler<SiardEvent> downloadAndArchiveDatabase(MFXStepper stepper) {
    return event -> {
      loadingSpinner.play();
      model.provideDatabaseArchiveMetaDataProperties(this);
      this.openLink.setOnMouseClicked(openArchiveDirectory(targetArchive));
      this.archivePath.setText(targetArchive.getAbsolutePath());
      try {
        controller.loadDatabase(targetArchive, false, handleDownloadSuccess(stepper), handleDownloadFailure(stepper));
      } catch (SQLException e) {
        // TODO should notify user about any error - Toast it # CR 458
        throw new RuntimeException(e);
      }
    };
  }

  private EventHandler<WorkerStateEvent> handleDownloadFailure(MFXStepper stepper) {
    return e -> {
      stepper.getStepperToggles().get(stepper.getCurrentIndex()).setState(StepperToggleState.ERROR);
      stepper.updateProgress();
      this.title.setVisible(false);
      loadingSpinner.hide();
      this.resultTitle.setVisible(true);
      I18n.bind(resultTitle.textProperty(), "archiveDownload.view.title.failed");
      I18n.bind(recordsLoaded.textProperty(), "archiveDownload.view.message.failed");
      resultTitle.getStyleClass().setAll("x-circle-icon", "h2", "label-icon-left");
      controller.closeDbConnection();
      this.buttonsBox = new StepperButtonBox().make(FAILED);
      addButtons(stepper);
    };
  }

  private EventHandler<WorkerStateEvent> handleDownloadSuccess(MFXStepper stepper) {
    return e -> {
      stepper.getStepperToggles().get(stepper.getCurrentIndex()).setState(StepperToggleState.COMPLETED);
      stepper.updateProgress();
      loadingSpinner.hide();
      this.title.setVisible(false);
      this.resultTitle.setVisible(true);
      this.subtitle1.setVisible(true);
      this.resultBox.setVisible(true);
      this.fileSystemBox.setVisible(true);
      I18n.bind(resultTitle.textProperty(), "archiveDownload.view.title.success");
      resultTitle.getStyleClass().setAll("ok-circle-icon", "h2", "label-icon-left");
//      setResultData();
      controller.closeDbConnection();
      stepper.fireEvent(new SiardEvent(DATABASE_DOWNLOADED));
      this.buttonsBox = new StepperButtonBox().make(DOWNLOAD_FINISHED);
      addButtons(stepper);
    };
  }

  private void addButtons(MFXStepper stepper) {
    this.borderPane.setBottom(buttonsBox);
    setListeners(stepper);
  }

  private EventHandler<MouseEvent> openArchiveDirectory(File file) {
    return event -> {
      try {
        new SystemFileBrowser(file).show();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  private void setResultData() {
    this.model.provideArchiveObject(this);
    this.subtitle1.setText(this.archive.getMetaData().getDbName());
    long total = 0;
    for (int i = 0; i < this.archive.getSchemas(); i++) {
      Schema schema = this.archive.getSchema(i);
      scrollBox.getChildren().add(new Label(schema.getMetaSchema().getName()));
      for (int y = 0; y < schema.getTables(); y++) {
        String tableName = schema.getTable(y).getMetaTable().getName();
        Long rows = schema.getTable(y).getMetaTable().getRows();
        addTableData(tableName, rows, y);
        total += rows;
      }
    }
    I18n.bind(recordsLoaded.textProperty(), "archiveDownload.view.message.success", total);
  }

  private void addTableData(String tableName, Long rows, Integer pos) {
    LabelIcon label = new LabelIcon(tableName, pos, IconView.IconType.OK);
    I18n.bind(label.textProperty(), "upload.result.success.table.rows", tableName, rows);
    scrollBox.getChildren().add(label);
  }

  @Override
  public void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
                    String databaseUsername, String databaseDescription, String databaseOwner, String databaseCreationDate,
                    LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive) {
    this.targetArchive = targetArchive;
  }

  @Override
  public void visit(SiardArchiveMetaData metaData) {
  }

  @Override
  public void visit(Archive archive) {
    this.archive = archive;
  }

  @Override
  public void visit(MetaData metaData) {

  }
}
