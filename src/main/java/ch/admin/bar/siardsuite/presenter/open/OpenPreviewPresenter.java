package ch.admin.bar.siardsuite.presenter.open;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.*;
import ch.admin.bar.siardsuite.presenter.PreviewPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.CANCEL;

public class OpenPreviewPresenter extends PreviewPresenter {

  @FXML
  protected Label title;
  @FXML
  protected Text text;
  @FXML
  protected StepperButtonBox buttonsBox;

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    super.init(controller, model, stage);

    title.textProperty().bind(I18n.createStringBinding("open.siard.archive.preview.title"));
    text.textProperty().bind(I18n.createStringBinding("open.siard.archive.preview.text"));

    initTreeView();

    buttonsBox = new StepperButtonBox().make(CANCEL);
    buttonsBox.cancel().setOnAction(event -> stage.navigate(View.START));

    this.borderPane.setBottom(buttonsBox);
  }

}
