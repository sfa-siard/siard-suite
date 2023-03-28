package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.Presenter;

public abstract class TreePresenter extends Presenter {
  @Override
  public void init(Controller controller, Model model, RootStage stage) { }

  public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) { }
}
