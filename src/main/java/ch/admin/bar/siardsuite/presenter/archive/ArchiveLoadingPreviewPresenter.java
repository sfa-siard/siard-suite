package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.*;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);

        I18n.bind(this.title.textProperty(),"archiveLoadingPreview.view.title");
        I18n.bind(this.text.textProperty(),"archiveLoadingPreview.view.text");
        this.loader.setImage(loading);
        new Spinner(this.loader).play();

        this.buttonsBox = new ButtonBox().make(CANCEL);
        this.borderPane.setBottom(buttonsBox);
        this.setListeners(stepper);
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.UPDATE_STEPPER_DBLOAD_EVENT, event -> {
            if (!event.isConsumed()) {
                scrollBox.getChildren().clear();

                try {
                    controller.loadDatabase(true, handleOnSuccess(stepper), handleOnFailure(stepper));
                    controller.addDatabaseLoadingValuePropertyListener((o1, oldValue, newValue) -> {
                        AtomicInteger pos = new AtomicInteger();
                        newValue.forEach(p -> addLoadingData(p.getKey(), pos.getAndIncrement()));
                    });

                    controller.addDatabaseLoadingProgressPropertyListener((o, oldValue, newValue) -> {
                        double pos = newValue.doubleValue();
                        progressBar.progressProperty().set(pos);
                    });

                } catch (SQLException e) {
                    e.printStackTrace();
                    fail(stepper, e, DATABASE_DOWNLOAD_FAILED);
                } finally {
                    event.consume();
                }
            }
        });

        this.buttonsBox.previous().setOnAction((event) -> cancel(stepper));
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
    }



    private EventHandler<WorkerStateEvent> handleOnFailure(MFXStepper stepper) {
        return e -> {
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

    private void addLoadingData(String text, Integer pos) {
        // set previous to ok
        if (scrollBox.getChildren().size() > 0) {
            int itemPos = scrollBox.getChildren().size() - 1;
            LabelIcon label = (LabelIcon) scrollBox.getChildren().get(itemPos);
            label.setGraphic(new IconView(itemPos, IconView.IconType.OK));
        }
        scrollBox.getChildren().add(
                new LabelIcon(text, pos, IconView.IconType.LOADING));
    }

    private void cancel(MFXStepper stepper) {
        stepper.previous();
        controller.cancelDownload();
    }

    private void fail(MFXStepper stepper, Throwable e, EventType<SiardEvent> event) {
        stepper.previous();
        this.stage.openDialog(View.ERROR_DIALOG);
        controller.cancelDownload();
        controller.failure(e.getLocalizedMessage());
        stepper.fireEvent(new SiardEvent(event));
    }
}
