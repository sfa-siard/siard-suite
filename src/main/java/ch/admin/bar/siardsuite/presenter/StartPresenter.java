package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import static ch.admin.bar.siardsuite.Workflow.*;
import static ch.admin.bar.siardsuite.util.I18n.bind;

public class StartPresenter extends Presenter {

    @FXML
    private ImageView dbImg;
    @FXML
    private ImageView archiveImg;
    @FXML
    private ImageView dbRightImg;
    @FXML
    private ImageView exportImg;
    @FXML
    private TextFlow flowCenter;
    @FXML
    private TextFlow flowRight;
    @FXML
    private TextFlow flowLeft;
    @FXML
    private ImageView archiveBubble;
    @FXML
    private ImageView archiveArrow;
    @FXML
    private ImageView uploadArrow;
    @FXML
    private ImageView uploadBubble;
    @FXML
    private ImageView exportArrow;
    @FXML
    private ImageView exportBubble;
    @FXML
    private Button archive;
    @FXML
    private Button upload;
    @FXML
    private Button export;
    @FXML
    private Button open;

    private final PathTransition transition = new PathTransition();

    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        resetImageViews();
        setListener();
        bindLabels();
    }

    private void bindLabels() {
        bind(flowLeft, "start.view.", "left");
        bind(flowRight, "start.view.", "right");
        bind(flowCenter, "start.view.", "center");
        bind(archive, "button.archive");
        bind(upload, "button.upload");
        bind(export, "button.export");
        bind(open, "button.open");
    }

    private void setListener() {
        this.archive.setOnAction(event -> this.controller.initializeWorkflow(ARCHIVE, stage));
        this.upload.setOnAction(event -> this.controller.initializeWorkflow(UPLOAD, stage));
        this.export.setOnAction(event -> this.controller.initializeWorkflow(EXPORT, stage));
        this.open.setOnAction(event -> this.controller.initializeWorkflow(OPEN, stage));

        archive.setOnMouseEntered(event -> {
            dbImg.setImage(Icon.siardDbRed);
            archiveImg.setImage(Icon.archiveRed);
            animateBubble(archiveArrow, archiveBubble);
        });

        archive.setOnMouseExited(event -> resetImageViews());

        upload.setOnMouseEntered(event -> {
            dbRightImg.setImage(Icon.siardDbRed);
            archiveImg.setImage(Icon.archiveRed);
            animateBubble(uploadArrow, uploadBubble);
        });

        upload.setOnMouseExited(event -> resetImageViews());

        export.setOnMouseEntered(event -> {
            exportImg.setImage(Icon.exportRed);
            archiveImg.setImage(Icon.archiveRed);
            animateBubble(exportArrow, exportBubble);
        });

        export.setOnMouseExited(event -> resetImageViews());

        open.setOnMouseMoved(event -> archiveImg.setImage(Icon.archiveRed));

        open.setOnMouseExited(event -> archiveImg.setImage(Icon.archive));
    }

    private void animateBubble(ImageView path, ImageView bubble) {
        Bounds bounds = path.localToScreen(path.getBoundsInLocal());
        bubble.setVisible(true);
        Line line = new Line(0, bounds.getHeight() / 2 - 10, bounds.getWidth() - 25, bounds.getHeight() / 2 - 10);
        transition.setNode(bubble);
        transition.setDuration(Duration.seconds(1));
        transition.setPath(line);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();
    }

    private void resetImageViews() {
        dbImg.setImage(Icon.siardDb);
        dbRightImg.setImage(Icon.siardDb);
        archiveImg.setImage(Icon.archive);
        exportImg.setImage(Icon.export);
        archiveBubble.setVisible(false);
        uploadBubble.setVisible(false);
        exportBubble.setVisible(false);
        transition.stop();
    }

}
