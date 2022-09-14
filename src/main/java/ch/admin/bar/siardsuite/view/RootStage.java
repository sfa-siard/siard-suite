package ch.admin.bar.siardsuite.view;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.presenter.RootPresenter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RootStage extends Stage {

  public RootStage(Controller controller) throws IOException {
    FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource("fxml/root-view.fxml"));
    Parent root = loader.load();
    loader.<RootPresenter>getController().init(controller, this);

    Scene scene = new Scene(root);
    scene.setFill(Color.WHITESMOKE);
    this.initStyle(StageStyle.DECORATED);
    this.setScene(scene);
    this.setTitle("SIARD Suite");
    this.show();
  }
}
