package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.component.IconView;
import ch.admin.bar.siardsuite.component.LabelIcon;
import ch.admin.bar.siardsuite.component.Spinner;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.FileUtils;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.model.View.ERROR_DIALOG;
import static ch.admin.bar.siardsuite.model.View.UPLOAD_ABORT_DIALOG;
import static ch.admin.bar.siardsuite.util.SiardEvent.*;

public class UploadingPresenter extends StepperPresenter {
  @FXML
  public Label title;
  @FXML
  public ImageView loader;
  @FXML
  public Label progress;
  @FXML
  public MFXButton cancel;
  @FXML
  public VBox scrollBox;
  @FXML
  public MFXProgressBar progressBar;

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    this.controller = controller;
    this.model = model;
    this.stage = stage;

    this.loader.setImage(Icon.loading);
    Spinner loadingSpinner = new Spinner(this.loader);
    loadingSpinner.play();
    I18n.bind(title.textProperty(), "upload.inProgress.title");
    I18n.bind(progress.textProperty(), "upload.records.uploaded.message");
    I18n.bind(cancel.textProperty(), "button.cancel");

    cancel.setOnAction(event -> stage.openDialog(UPLOAD_ABORT_DIALOG)); // TODO: how to cancel the upload task
  }

  @Override
  public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
    this.init(controller, model, stage);
    stepper.addEventHandler(SiardEvent.UPLOAD_CONNECTION_UPDATED, uploadDatabase(stepper));
  }

  private EventHandler<SiardEvent> uploadDatabase(MFXStepper stepper) {
    return event -> {
      if (!event.isConsumed()) {
        scrollBox.getChildren().clear();

        EventHandler<WorkerStateEvent> onSuccess = e -> {
          stepper.next();
          stepper.fireEvent(new SiardEvent(UPLOAD_SUCCEDED));
          controller.releaseResources();
        };

        EventHandler<WorkerStateEvent> onFailure = e -> {
          System.err.println( I18n.get( "upload.result.failed.title") + " " + e.getSource().getException());
          e.getSource().getException().printStackTrace();
          stepper.next();
          controller.cancelUpload();
          stepper.fireEvent(new SiardEvent(UPLOAD_FAILED));
        };

        try {
          controller.uploadArchive(onSuccess, onFailure);
        } catch (Exception e) {
          fail(e, stepper);
        }

        controller.addDatabaseUploadingValuePropertyListener((o, oldValue, newValue) -> {
          AtomicInteger pos1 = new AtomicInteger();
          addLoadingData(newValue, pos1.getAndIncrement());
        });
        controller.addDatabaseUploadingProgressPropertyListener((o, oldValue, newValue) -> {
          double pos = newValue.doubleValue();
          progressBar.progressProperty().set(pos);
        });
        event.consume();
      }
    };
  }

  private void fail(Throwable e, MFXStepper stepper) {
    e.printStackTrace();
    stepper.previous();
    controller.cancelUpload();
    controller.failure(new Failure(e));
    this.stage.openDialog(ERROR_DIALOG);
    this.stage.fireEvent(new SiardEvent(ERROR_OCCURED));
  }

  private void addLoadingData(String text, Integer pos) {
    // set previous to ok
    if (scrollBox.getChildren().size() > 0) {
      int itemPos = scrollBox.getChildren().size() - 1;
      LabelIcon label = (LabelIcon) scrollBox.getChildren().get(itemPos);
      label.setGraphic(new IconView(itemPos, IconView.IconType.OK));
    }
    scrollBox.getChildren().add(
            new LabelIcon(text, pos, IconView.IconType.LOADING));
  }
}
