package ch.admin.bar.siardsuite;

import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.enterag.utils.ProgramInfo;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class SiardViewExplorer extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Model model = new Model();
        Controller controller = new Controller(model);

        ProgramInfo programInfo = ProgramInfo.getProgramInfo(
                "SIARD View Explorer","0",
                "View Explorer","0",
                "Show individual views from your application...",
                "Swiss Federal Archives, Berne, Switzerland, 2007-2022");


        RootStage rootStage = new RootStage(model, controller);
        // change the view that you want to see. note: the stepper is not yet supported
        rootStage.navigate(View.ARCHIVE_METADATA.getName());
    }

    public static void main(String[] args) {
        launch();
    }
}
