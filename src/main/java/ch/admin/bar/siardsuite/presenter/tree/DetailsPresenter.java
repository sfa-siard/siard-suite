package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * Present details of an element in the tree, e.g. details of a column, a routine parameter...
 */
public abstract class DetailsPresenter extends Presenter {

    @FXML
    protected VBox texts;

    @FXML
    protected VBox labels;

    @Override
    public void init(Controller controller, RootStage stage) {
    }

    public void init(Controller controller, RootStage stage, TreeAttributeWrapper wrapper) {
        this.controller = controller;
        this.stage = stage;

        bindLabels();
    }

    protected abstract void bindLabels();

}
