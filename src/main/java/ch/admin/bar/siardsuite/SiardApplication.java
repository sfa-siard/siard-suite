package ch.admin.bar.siardsuite;

import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class SiardApplication extends Application {

  @Override
  public void start(Stage stage) throws IOException {

    Controller controller = new Controller();

    new RootStage(controller);
  }

  public static void main(String[] args) {
    launch();
  }
}
