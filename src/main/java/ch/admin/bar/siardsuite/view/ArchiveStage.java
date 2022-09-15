package ch.admin.bar.siardsuite.view;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.presenter.ArchivePresenter;
import ch.admin.bar.siardsuite.presenter.RootPresenter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ArchiveStage extends Stage {

  public ArchiveStage(Controller controller) throws IOException {
    FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource("fxml/archive-view.fxml"));
    Parent root = loader.load();
    loader.<ArchivePresenter>getController().init(controller, this);

    Scene scene = new Scene(root);
    scene.setFill(Color.WHITESMOKE);
    this.initStyle(StageStyle.UNDECORATED);
    this.setScene(scene);
    this.show();
  }
}
