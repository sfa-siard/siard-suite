package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.*;
import ch.admin.bar.siardsuite.presenter.PreviewPresenter;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class ArchivePreviewPresenter extends PreviewPresenter {
  @FXML
  protected Label title;
  @FXML
  protected Text text;
  @FXML
  protected StepperButtonBox buttonsBox;

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    super.init(controller, model, stage);

    this.title.textProperty().bind(I18n.createStringBinding("archivePreview.view.title"));
    this.text.textProperty().bind(I18n.createStringBinding("archivePreview.view.text"));

    this.buttonsBox = new StepperButtonBox().make(StepperButtonBox.DEFAULT);
    this.borderPane.setBottom(buttonsBox);
  }

  public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
    init(controller, model, stage);
    this.setListeners(stepper);
  }

  protected void setListeners(MFXStepper stepper) {
    setListeners();

    stepper.addEventHandler(SiardEvent.ARCHIVE_LOADED, event -> initTreeView());

    this.buttonsBox.next().setOnAction((event) -> stepper.next());
    this.buttonsBox.previous().setOnAction((event) -> {
      stepper.previous(); // Step over hidden loading step
      stepper.previous();
      this.stage.setHeight(1080.00);
    });
    this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG.getName()));
  }

}
