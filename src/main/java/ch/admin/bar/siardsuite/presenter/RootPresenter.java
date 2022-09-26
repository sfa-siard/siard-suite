package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Locale;

public class RootPresenter extends Presenter {

  @FXML
  public Menu menuItemLanguage;
  @FXML
  public MenuItem menuItemOptions, menuItemInfo, menuItemClose;
  private double xOffset;
  private double yOffset;

  @FXML
  public HBox windowHeader;

  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    windowHeader.setOnMousePressed(event -> {
      xOffset = stage.getX() - event.getScreenX();
      yOffset = stage.getY() - event.getScreenY();
    });

    windowHeader.setOnMouseDragged(event -> {
      stage.setX(event.getScreenX() + xOffset);
      stage.setY(event.getScreenY() + yOffset);
    });

    this.stage.titleProperty().bind(I18n.createStringBinding("window.title"));

    initMenu();
  }

  private void initMenu() {
    this.menuItemLanguage.textProperty().bind(I18n.createStringBinding("menu.item.language"));
    this.menuItemOptions.textProperty().bind(I18n.createStringBinding("menu.item.options"));
    this.menuItemInfo.textProperty().bind(I18n.createStringBinding("menu.item.info"));
    this.menuItemClose.textProperty().bind(I18n.createStringBinding("menu.item.close"));

    // Language
    I18n.getSupportedLocales().forEach((locale) -> {
      CheckMenuItem item = new CheckMenuItem();
      item.setId(locale.toString());
      item.textProperty().bind(I18n.createStringBinding(locale.toString()));
      this.menuItemLanguage.getItems().add(item);
      item.setOnAction(event -> {
        CheckMenuItem cmi = (CheckMenuItem) event.getSource();
        I18n.setLocale(new Locale(cmi.getId()));
      });
    });

    this.menuItemClose.setOnAction(event -> this.stage.close());

  }
}
