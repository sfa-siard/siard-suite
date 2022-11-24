package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
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
  @FXML
  public MenuBar menuBar;
  @FXML
  public Menu menuContainer;

  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    allowStageRepositioning(windowHeader);

    I18n.bind(stage.titleProperty(),"window.title");

    initMenu();
  }

  private void initMenu() {
    this.menuBar.setOnMouseClicked(event -> this.menuContainer.show());

    I18n.bind(menuItemLanguage.textProperty(),"menu.item.language");
    I18n.bind(menuItemOptions.textProperty(),"menu.item.options");
    I18n.bind(menuItemInfo.textProperty(),"menu.item.info");
    I18n.bind(menuItemClose.textProperty(),"menu.item.close");

    // Language
    ToggleGroup items = new ToggleGroup();
    I18n.getSupportedLocales().forEach((locale) -> {
      RadioMenuItem item = new RadioMenuItem();
      item.setId(locale.toString());
      I18n.bind(item.textProperty(),locale.toString());
      this.menuItemLanguage.getItems().add(item);
      item.setOnAction(event -> {
        RadioMenuItem cmi = (RadioMenuItem) event.getSource();
        I18n.setLocale(new Locale(cmi.getId()));
        stage.fireEvent(new SiardEvent(SiardEvent.UPDATE_LANGUAGE_EVENT));
      });
      item.setToggleGroup(items);
    });

    this.menuItemClose.setOnAction(event -> this.stage.close());
    this.menuItemInfo.setOnAction(event -> this.stage.openDialog(View.INFO_DIALOG));
    this.menuItemOptions.setOnAction(event -> this.stage.openDialog(View.OPTION_DIALOG));

  }

}
