package ch.admin.bar.siardsuite.view;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.ArchivePresenter;
import ch.admin.bar.siardsuite.presenter.RootPresenter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ArchiveStage extends Stage {

  private Model model;
  private Controller controller;

  public ArchiveStage(Model model, Controller controller) throws IOException {
    this.model = model;
    this.controller = controller;
    FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource("fxml/archive-view.fxml"));
    AnchorPane anchorPane = loader.load();
    loader.<ArchivePresenter>getController().init(controller, model, this);

    Scene scene = new Scene(anchorPane);
    scene.setFill(Color.WHITESMOKE);
    this.initStyle(StageStyle.UNDECORATED);
    this.setScene(scene);
    this.show();
  }
}
