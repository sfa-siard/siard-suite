package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RootPresenter implements Presenter {

  public AnchorPane rootPane;
  private Controller controller;

  private Stage stage;

  @FXML
  private Text welcomeText;

  public void init(Controller controller, Stage stage) {
    this.controller = controller;
    this.stage = stage;

    welcomeText.setText("Welcome to SIARD Suite!");
  }
}
