package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.view.animations.Animation;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;

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

    Animation animation = new Animation(new PathTransition());

    public void init(Controller controller, RootStage stage) {
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
            animation.start(archiveArrow, archiveBubble);
        });
        archive.setOnMouseExited(event -> resetImageViews());

        upload.setOnMouseEntered(event -> {
            dbRightImg.setImage(Icon.siardDbRed);
            archiveImg.setImage(Icon.archiveRed);
            animation.start(uploadArrow, uploadBubble);
        });
        upload.setOnMouseExited(event -> resetImageViews());

        export.setOnMouseEntered(event -> {
            exportImg.setImage(Icon.exportRed);
            archiveImg.setImage(Icon.archiveRed);
            animation.start(exportArrow, exportBubble);
        });
        export.setOnMouseExited(event -> resetImageViews());

        open.setOnMouseMoved(event -> archiveImg.setImage(Icon.archiveRed));
        open.setOnMouseExited(event -> archiveImg.setImage(Icon.archive));
    }

    private void resetImageViews() {
        animation.stop();
        dbImg.setImage(Icon.siardDb);
        dbRightImg.setImage(Icon.siardDb);
        archiveImg.setImage(Icon.archive);
        exportImg.setImage(Icon.export);
        archiveBubble.setVisible(false);
        uploadBubble.setVisible(false);
        exportBubble.setVisible(false);
    }

}
