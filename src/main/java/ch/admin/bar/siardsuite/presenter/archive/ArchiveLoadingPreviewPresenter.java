package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.*;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
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

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.CANCEL;
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
    public Label subtitle;
    @FXML
    public VBox scrollBox;
    @FXML
    private StepperButtonBox buttonsBox;

    private final Image loading = Icon.loading;
    private final Image ok = Icon.ok;

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
        this.subtitle.textProperty().bind(I18n.createStringBinding("archiveLoadingPreview.view.subtitle"));
        this.loader.setImage(loading);
        new Spinner(this.loader).play();

        this.buttonsBox = new StepperButtonBox().make(CANCEL);
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

            // TODO this part is never reached because of onlyMetaData-Load
            // There is no progress in Metadata-Load # CR 460
//            controller.addDatabaseLoadingValuePropertyListener((o1, oldValue1, newValue1) -> {
//                AtomicInteger pos1 = new AtomicInteger();
//                newValue1.forEach(s -> addTableData(s.getKey(), pos1.getAndIncrement()));
//            });
//
//            controller.addDatabaseLoadingProgressPropertyListener((o, oldValue, newValue) -> {
//                double pos = newValue.doubleValue() * (scrollBox.getChildren().size() - 1);
//
//                    LabelIcon label = (LabelIcon) scrollBox.getChildren().get((int) pos);
//                    label.setGraphic(new IconView(newValue.intValue(), IconView.IconType.OK));
//                }
//            });

        });

        this.buttonsBox.previous().setOnAction((event) -> {
            controller.closeDbConnection();
            stepper.previous();

        });
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
    }

//    private void addTableData(String tableName, Integer pos) {
//        scrollBox.getChildren().add(
//                new LabelIcon(tableName, pos, IconView.IconType.LOADING));
//    }

}
