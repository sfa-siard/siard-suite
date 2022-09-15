package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import javafx.stage.Stage;

public interface Presenter {

  void init(Controller controller, Model model, Stage stage);
}
