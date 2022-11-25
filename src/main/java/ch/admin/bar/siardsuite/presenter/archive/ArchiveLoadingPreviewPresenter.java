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

        this.title.textProperty().bind(I18n.createStringBinding("archiveLoadingPreview.view.title"));
        this.text.textProperty().bind(I18n.createStringBinding("archiveLoadingPreview.view.text"));
        this.loader.setImage(loading);
        new Spinner(this.loader).play();

        this.buttonsBox = new ButtonBox().make(CANCEL);
        this.borderPane.setBottom(buttonsBox);
        this.setListeners(stepper);
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.UPDATE_STEPPER_DBLOAD_EVENT, event -> {
            scrollBox.getChildren().clear();

            EventHandler<WorkerStateEvent> onSuccess = e -> {
                controller.closeDbConnection();
                stepper.next();
                stepper.fireEvent(new SiardEvent(ARCHIVE_LOADED));
            };

            EventHandler<WorkerStateEvent> onFailure = e -> {
                controller.closeDbConnection();
                stepper.previous();
            };

            try {
                controller.loadDatabase(true, onSuccess, onFailure);
            } catch (SQLException e) {
                // TODO should notify user about any error - Toast it # CR 458
                stepper.previous();
                throw new RuntimeException(e);
            }

            controller.addDatabaseLoadingValuePropertyListener((o1, oldValue, newValue) -> {
                AtomicInteger pos = new AtomicInteger();
                newValue.forEach(p -> addLoadingData(p.getKey(), pos.getAndIncrement()));
            });

            controller.addDatabaseLoadingProgressPropertyListener((o, oldValue, newValue) -> {
                double pos = newValue.doubleValue();
                progressBar.progressProperty().set(pos);
            });

        });

        this.buttonsBox.previous().setOnAction((event) -> {
            controller.closeDbConnection();
            stepper.previous();

        });
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
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

}
