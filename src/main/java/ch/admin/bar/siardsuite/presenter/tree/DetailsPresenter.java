package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
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
    public void init(Controller controller, Model model, RootStage stage) {
    }

    public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        bindLabels();

        model.populate(texts, wrapper.getDatabaseObject(), wrapper.getType());
    }

    protected abstract void bindLabels();

}
