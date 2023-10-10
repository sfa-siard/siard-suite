package ch.admin.bar.siardsuite.view;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.presenter.RootPresenter;
import ch.admin.bar.siardsuite.presenter.search.SearchTableDialogPresenter;
import ch.admin.bar.siardsuite.util.CastHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.val;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public class RootStage extends Stage {
  private final Controller controller;

  @FXML
  private final BorderPane rootPane;
  @FXML
  private final BorderPane dialogPane;

  public RootStage(Controller controller) throws IOException {
    this.controller = controller;

    FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(View.ROOT.getName()));
    rootPane = loader.load();
    loader.<RootPresenter>getController().init(controller,this);

    // load start view
    navigate(controller.getCurrentView());

    // prepare for dialogs
    loader = new FXMLLoader(SiardApplication.class.getResource(View.DIALOG.getName()));
    dialogPane = loader.load();
    loader.<DialogPresenter>getController().init(controller, this);
    dialogPane.setVisible(false);

    // set overall stack pane
    StackPane stackPane = new StackPane(rootPane, dialogPane);

    Scene scene = new Scene(stackPane);
    scene.getRoot().getStyleClass().add("root");
    scene.setFill(null);

    this.setMaximized(true);
    this.initStyle(StageStyle.DECORATED);
    this.getIcons().add(Icon.archiveRed);
    this.setScene(scene);
    this.show();
  }

  private Presenter setCenter(BorderPane borderPane, String viewName) {
    FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(viewName));
    try {
      borderPane.setCenter(loader.load());
      val presenter = loader.<Presenter>getController();
      presenter.init(this.controller, this);

      return presenter;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void navigate(View view) {
    controller.setCurrentView(view);
    setCenter(rootPane, view.getName());
  }

  public void openDialog(View view) {
    setCenter(dialogPane, view.getName());
    dialogPane.setVisible(true);
  }

  public <T> T openDialogAndReturnPresenter(View view, Class<T> presenterClass) {
    val presenter = setCenter(dialogPane, view.getName());
    dialogPane.setVisible(true);

    return CastHelper.tryCast(presenter, presenterClass)
            .orElseThrow(() -> new IllegalArgumentException(
                    String.format(
                            "Presenter type %s is not supported by view %s",
                            presenterClass,
                            view.getName()
                    )));
  }

  public void openSearchTableDialog(Consumer<Optional<String>> searchTermConsumer) {
    val presenter = openDialogAndReturnPresenter(
            View.SEARCH_TABLE_DIALOG,
            SearchTableDialogPresenter.class);

    presenter.registerResultListener(searchTermConsumer);
  }

  public void closeDialog() {
    dialogPane.setVisible(false);
  }
}
