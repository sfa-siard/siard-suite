package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.Locale;

public class RootPresenter extends Presenter {

  @FXML
  public Menu menuItemLanguage;
  @FXML
  public MenuItem menuItemOptions, menuItemInfo, menuItemClose;

  @FXML
  public HBox windowHeader;

  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    allowStageRepositioning(windowHeader);

    this.stage.titleProperty().bind(I18n.createStringBinding("window.title"));

    initMenu();
  }

  private void initMenu() {
    this.menuItemLanguage.textProperty().bind(I18n.createStringBinding("menu.item.language"));
    this.menuItemOptions.textProperty().bind(I18n.createStringBinding("menu.item.options"));
    this.menuItemInfo.textProperty().bind(I18n.createStringBinding("menu.item.info"));
    this.menuItemClose.textProperty().bind(I18n.createStringBinding("menu.item.close"));

    // Language
    ToggleGroup items = new ToggleGroup();
    I18n.getSupportedLocales().forEach((locale) -> {
      RadioMenuItem item = new RadioMenuItem();
      item.setId(locale.toString());
      item.textProperty().bind(I18n.createStringBinding(locale.toString()));
      this.menuItemLanguage.getItems().add(item);
      item.setOnAction(event -> {
        RadioMenuItem cmi = (RadioMenuItem) event.getSource();
        I18n.setLocale(new Locale(cmi.getId()));
        stage.fireEvent(new SiardEvent(SiardEvent.UPDATE_LANGUAGE_EVENT));
      });
      item.setToggleGroup(items);
    });

    this.menuItemClose.setOnAction(event -> this.stage.close());

  }

}
