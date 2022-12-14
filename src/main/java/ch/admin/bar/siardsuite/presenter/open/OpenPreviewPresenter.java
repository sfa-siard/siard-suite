package ch.admin.bar.siardsuite.presenter.open;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.PreviewPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import static ch.admin.bar.siardsuite.Workflow.EXPORT;
import static ch.admin.bar.siardsuite.Workflow.UPLOAD;
import static ch.admin.bar.siardsuite.component.ButtonBox.Type.OPEN_PREVIEW;

public class OpenPreviewPresenter extends PreviewPresenter {

  @FXML
  protected Label title;
  @FXML
  protected Text text;
  @FXML
  protected ButtonBox buttonsBox;

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    super.init(controller, model, stage);

    I18n.bind(title.textProperty(), "open.siard.archive.preview.title");
    I18n.bind(text.textProperty(), "open.siard.archive.preview.text");

    initTreeView();

    this.buttonsBox = new ButtonBox().make(OPEN_PREVIEW);
    this.borderPane.setBottom(buttonsBox);

    buttonsBox.cancel().setOnAction(event -> {
      this.controller.setWorkflow(UPLOAD);
      stage.openDialog(View.OPEN_SIARD_ARCHIVE_DIALOG);
    });
    buttonsBox.previous().setOnAction(event -> {
      this.controller.setWorkflow(EXPORT);
      this.stage.openDialog(View.EXPORT_SELECT_TABLES);
    });
    buttonsBox.next().setOnAction(event -> this.stage.navigate(View.START));
    setListeners();
  }

}
