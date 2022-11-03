package ch.admin.bar.siardsuite.presenter.open;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.*;
import ch.admin.bar.siardsuite.presenter.PreviewPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class OpenPreviewPresenter extends PreviewPresenter {

  @FXML
  protected Label title;
  @FXML
  protected Text text;
  @FXML
  public MFXButton cancelButton;

  @FXML
  public MFXButton exportButton;
  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    super.init(controller, model, stage);

    title.textProperty().bind(I18n.createStringBinding("open.siard.archive.preview.title"));
    text.textProperty().bind(I18n.createStringBinding("open.siard.archive.preview.text"));

    initTreeView();

    cancelButton.setOnAction(event -> this.stage.navigate(View.START));
    cancelButton.textProperty().bind(I18n.createStringBinding("button.cancel"));

    exportButton.setOnAction(event -> this.stage.openDialog(View.EXPORT_SELECT_TABLES));
    exportButton.textProperty().bind(I18n.createStringBinding("button.export"));
    setListeners();
  }

}
