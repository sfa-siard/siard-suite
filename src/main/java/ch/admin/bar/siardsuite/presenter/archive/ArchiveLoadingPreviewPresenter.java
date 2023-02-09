package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.*;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.ProgressItem;
import ch.admin.bar.siardsuite.presenter.ProgressItems;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.CANCEL;
import static ch.admin.bar.siardsuite.util.SiardEvent.ARCHIVE_LOADED;
import static ch.admin.bar.siardsuite.util.SiardEvent.DATABASE_DOWNLOAD_FAILED;

public class ArchiveLoadingPreviewPresenter extends StepperPresenter {
    @FXML
    public Label title;
    @FXML
    public Text text;
    @FXML
    public VBox leftVBox;
    @FXML
    public BorderPane borderPane;
    @FXML
    public ImageView loader;
    @FXML
    public VBox scrollBox;
    @FXML
    public MFXProgressBar progressBar;
    @FXML
    private ButtonBox buttonsBox;

    private final Image loading = Icon.loading;

    private final ProgressItems progressItems = new ProgressItems();

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);

        I18n.bind(this.title.textProperty(), "archiveLoadingPreview.view.title");
        I18n.bind(this.text.textProperty(), "archiveLoadingPreview.view.text");
        this.loader.setImage(loading);
        new Spinner(this.loader).play();

        this.buttonsBox = new ButtonBox().make(CANCEL);
        this.borderPane.setBottom(buttonsBox);

        this.buttonsBox.previous().setOnAction((event) -> cancel(stepper));
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));

        this.setListeners(stepper);
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.UPDATE_STEPPER_DBLOAD_EVENT, event -> {
            if (!event.isConsumed()) {
                scrollBox.getChildren().clear();

                try {
                    controller.loadDatabase(true, handleOnSuccess(stepper), handleOnFailure(stepper));
                    controller.addDatabaseLoadingValuePropertyListener(databaseLoadingValuePropertyListener);
                    controller.addDatabaseLoadingProgressPropertyListener(numberChangeListener);
                } catch (Exception e) {
                    fail(stepper, e, DATABASE_DOWNLOAD_FAILED);
                } finally {
                    event.consume();
                }
            }
        });
    }

    private void addLoadingItem(String text, Integer pos) {
        ObservableList<Node> children = scrollBox.getChildren();
        setPreviousItemToOk(children);
        ProgressItem newItem = new ProgressItem(pos, text);
        progressItems.add(newItem);
        children.add(newItem.icon());
    }

    private void setPreviousItemToOk(ObservableList<Node> children) {
        if (children.size() > 0) {
            ProgressItem previous = this.progressItems.last();
            previous.ok();
            /*int itemPos = children.size() - 1;
            LabelIcon label = (LabelIcon) children.get(itemPos);
            label.setGraphic(new IconView(itemPos, IconView.IconType.OK));*/
        }
    }

    private void cancel(MFXStepper stepper) {
        stepper.previous();
        controller.cancelDownload();
    }

    private void fail(MFXStepper stepper, Throwable e, EventType<SiardEvent> event) {
        stepper.previous();
        e.printStackTrace();
        this.stage.openDialog(View.ERROR_DIALOG);
        controller.cancelDownload();
        controller.failure(new Failure(e));
        stepper.fireEvent(new SiardEvent(event));
    }

    private EventHandler<WorkerStateEvent> handleOnFailure(MFXStepper stepper) {
        return e -> {
            e.getSource().getException().printStackTrace();
            fail(stepper, e.getSource().getException(), DATABASE_DOWNLOAD_FAILED);
        };
    }

    private EventHandler<WorkerStateEvent> handleOnSuccess(MFXStepper stepper) {
        return e -> {
            stepper.next();
            stepper.fireEvent(new SiardEvent(ARCHIVE_LOADED));
            controller.releaseResources();
        };
    }


    private final ChangeListener<ObservableList<Pair<String, Long>>> databaseLoadingValuePropertyListener = (o1, oldValue, newValue) -> {
        newValue.forEach(p -> addLoadingItem(p.getKey(), new AtomicInteger().getAndIncrement()));
    };

    private final ChangeListener<Number> numberChangeListener = (o, oldValue, newValue) -> {
        progressBar.progressProperty().set(newValue.doubleValue());
    };
}
