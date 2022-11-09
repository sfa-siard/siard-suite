package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.ui.Icon;
import ch.admin.bar.siardsuite.ui.Spinner;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.model.View.UPLOAD_ABORT_DIALOG;
import static ch.admin.bar.siardsuite.util.SiardEvent.UPLOAD_FAILED;
import static ch.admin.bar.siardsuite.util.SiardEvent.UPLOAD_SUCCEDED;

public class UploadingPresenter  extends StepperPresenter {
    @FXML
    public Label title;
    @FXML
    public ImageView loader;
    @FXML
    public Label progress;
    @FXML
    public MFXButton cancel;
    @FXML
    public VBox scrollBox;

    private Spinner loadingSpinner;

    List<DatabaseTable> data;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.controller = controller;
        this.model = model;
        this.stage = stage;

        this.loader.setImage(Icon.loading);
        loadingSpinner = new Spinner(this.loader);
        loadingSpinner.play();
        I18n.bind(title.textProperty(), "upload.inProgress.title");
        I18n.bind(progress.textProperty(), "upload.records.uploaded.message");
        I18n.bind(cancel.textProperty(), "button.cancel");

        cancel.setOnAction(event -> stage.openDialog(UPLOAD_ABORT_DIALOG)); // TODO: how to cancel the upload task
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
        stepper.addEventHandler(SiardEvent.UPLOAD_CONNECTION_UPDATED, uploadDatabase(stepper));
    }

    private EventHandler<SiardEvent> uploadDatabase(MFXStepper stepper) {
        return event -> {
            scrollBox.getChildren().clear();

            EventHandler<WorkerStateEvent> onSuccess = e -> {
                controller.closeDbConnection();
                stepper.next();
                stepper.fireEvent(new SiardEvent(UPLOAD_SUCCEDED));
            };

            EventHandler<WorkerStateEvent> onFailure = e -> {
                controller.closeDbConnection();
                stepper.next();
                stepper.fireEvent(new SiardEvent(UPLOAD_FAILED));
            };

            try {
                controller.uploadArchive(onSuccess, onFailure);
            } catch (SQLException e) {
                navigateBack(stepper);
                throw new RuntimeException(e);
            }

            controller.addDatabaseLoadingValuePropertyListener((o1, oldValue1, newValue1) -> {
                AtomicInteger pos1 = new AtomicInteger();
                newValue1.forEach(s -> addTableData(s, pos1.getAndIncrement()));
            });

            controller.addDatabaseLoadingProgressPropertyListener((o, oldValue, newValue) -> {
                double pos = newValue.doubleValue() * (scrollBox.getChildren().size() - 1);
                if (pos >= 1) {
                    Label label = (Label) scrollBox.getChildren().get((int) pos);
                    label.setGraphic(getImageView(newValue.intValue(), Icon.ok));
                }
            });

        };
    }

    private void addTableData(String tableName, Integer pos) {
        Label label = new Label(tableName);
        label.getStyleClass().add("view-text");
        label.setContentDisplay(ContentDisplay.RIGHT);
        ImageView imageView = getImageView(pos, Icon.loading);
        new Spinner(imageView).play();
        label.setGraphic(imageView);
        scrollBox.getChildren().add(label);
    }

    private ImageView getImageView(Integer pos, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(14.0);
        imageView.setFitWidth(14.0);
        imageView.getStyleClass().addAll("loading-icon", "icon-button");
        imageView.setId("dataLoader" + pos);
        return imageView;
    }
    private void navigateBack(MFXStepper stepper) {
        stepper.previous();
        this.stage.setHeight(1080.00);
    }
}
