package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import lombok.val;

public class DialogPresenter extends Presenter {

  @FXML
  protected HBox windowHeader;

  @Override
  public void init(Controller controller, RootStage stage) {
    this.controller = controller;
    this.stage = stage;

    allowStageRepositioning(windowHeader);
  }

  public static LoadedFxml<DialogPresenter> load(
          final Controller controller,
          final RootStage rootStage
  ) {
    val loaded = FXMLLoadHelper.<DialogPresenter>load("fxml/dialog.fxml");
    loaded.getController().init(
            controller,
            rootStage);

    return loaded;
  }
}
