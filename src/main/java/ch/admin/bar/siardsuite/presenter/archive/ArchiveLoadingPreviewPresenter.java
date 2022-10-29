package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.ui.Icon;
import ch.admin.bar.siardsuite.ui.Spinner;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.CANCEL;

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

    List<DatabaseTable> data;

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
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        this.title.textProperty().bind(I18n.createStringBinding("archiveLoadingPreview.view.title"));
        this.text.textProperty().bind(I18n.createStringBinding("archiveLoadingPreview.view.text"));
        this.subtitle.textProperty().bind(I18n.createStringBinding("archiveLoadingPreview.view.subtitle"));
        this.loader.setImage(loading);
        new Spinner(this.loader).play();

        this.buttonsBox = new StepperButtonBox().make(CANCEL);
        this.borderPane.setBottom(buttonsBox);
        this.setListeners(stepper);
    }

    private void addTableData(String tableName, Integer pos) {
        Label label = new Label(tableName);
        label.getStyleClass().add("view-text");
        label.setContentDisplay(ContentDisplay.RIGHT);
        ImageView imageView = getImageView(pos, loading);
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

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.UPDATE_STEPPER_DBLOAD_EVENT, event -> {
            scrollBox.getChildren().clear();

            EventHandler<WorkerStateEvent> onSuccess = e -> {
                controller.closeDbConnection();
                stepper.next();
                stepper.fireEvent(getUpdateEvent(SiardEvent.ARCHIVE_LOADED));
                this.stage.setHeight(950);
            };

            EventHandler<WorkerStateEvent> onFailure = e -> {
                controller.closeDbConnection();
                stepper.previous();
                this.stage.setHeight(1080.00);
            };

            controller.loadDatabase(true, onSuccess, onFailure);

            controller.addDatabaseLoadingValuePropertyListener((o1, oldValue1, newValue1) -> {
                AtomicInteger pos1 = new AtomicInteger();
                newValue1.forEach(t -> addTableData(t.getName(), pos1.getAndIncrement()));
            });

            controller.addDatabaseLoadingProgressPropertyListener((o, oldValue, newValue) -> {
                double pos = newValue.doubleValue() * (scrollBox.getChildren().size() - 1);
                if (pos >= 1) {
                    Label label = (Label) scrollBox.getChildren().get((int) pos);
                    label.setGraphic(getImageView(newValue.intValue(), ok));
                }
            });



        });

        this.buttonsBox.previous().setOnAction((event) -> {
            controller.closeDbConnection();
            stepper.previous();
            this.data.clear();
            this.stage.setHeight(1080.00);
        });
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG.getName()));
    }
}
