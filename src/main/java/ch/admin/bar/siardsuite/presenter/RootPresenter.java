package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Locale;

public class RootPresenter implements Presenter {

  public AnchorPane rootPane;
  public Button buttonGerman;
  public Button buttonEnglish;
  private Controller controller;

  private Stage stage;

  @FXML
  private Text welcomeText;

  public void init(Controller controller, Stage stage) {

    this.controller = controller;
    this.stage = stage;

    this.stage.titleProperty().bind(I18n.createStringBinding("window.title"));
    welcomeText.textProperty().bind(I18n.createStringBinding("welcome.text"));
    
    buttonEnglish.textProperty().bind(I18n.createStringBinding("language.en"));
    buttonGerman.textProperty().bind(I18n.createStringBinding("language.de"));
    buttonEnglish.setOnAction((evt) -> I18n.setLocale(Locale.ENGLISH));
    buttonGerman.setOnAction((evt) -> I18n.setLocale(Locale.GERMAN));

  }
}
