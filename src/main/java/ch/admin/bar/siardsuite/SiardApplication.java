package ch.admin.bar.siardsuite;

import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.enterag.utils.ProgramInfo;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class SiardApplication extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    Model model = new Model();
    Controller controller = new Controller(model);
    // trial to fix the bad font rendering issue from javafx
    System.setProperty("prism.lcdtext", "false");
    System.setProperty("prism.text", "t2k");

    // needed for the api *eyes rolling*
    ProgramInfo programInfo = ProgramInfo.getProgramInfo(
            "SIARD Suite",getClass().getPackage().getImplementationVersion(),
            "SiardGui","0",
            "Program to download, view, upload database content and database edit meta data in a .siard file",
            "Swiss Federal Archives, Berne, Switzerland, 2007-2022");

    new RootStage(model, controller);
  }

  public static void main(String[] args) {
    launch();
  }

}
