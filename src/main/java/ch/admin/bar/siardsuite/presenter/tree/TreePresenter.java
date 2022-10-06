package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.TreeContentViewModel;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.view.RootStage;

public abstract class TreePresenter extends Presenter {
  @Override
  public void init(Controller controller, Model model, RootStage stage) { }

  public void init(Controller controller, TreeContentViewModel model, RootStage stage) { }
}
