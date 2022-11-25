package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import static ch.admin.bar.siardsuite.Workflow.*;

public class StartPresenter extends Presenter {

    @FXML
    public ImageView dbImg;
    @FXML
    public ImageView archiveImg;
    @FXML
    public ImageView dbRightImg;
    @FXML
    public ImageView exportImg;
    @FXML
    public TextFlow flowCenter;
    @FXML
    public TextFlow flowRight;
    @FXML
    public TextFlow flowLeft;
    @FXML
    public ImageView archiveBubble;
    @FXML
    public ImageView archiveArrow;
    @FXML
    public ImageView uploadArrow;
    @FXML
    public ImageView uploadBubble;
    @FXML
    public ImageView exportArrow;
    @FXML
    public ImageView exportBubble;
    @FXML
    private Button archive;
    @FXML
    private Button upload;
    @FXML
    private Button export;
    @FXML
    private Button open;

    final PathTransition transition = new PathTransition();

    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        resetImageViews();
        setListener();
        bindTextFlows(flowLeft, "left");
        bindTextFlows(flowRight, "right");
        bindTextFlows(flowCenter, "center");
        I18n.bind(archive.textProperty(),"button.archive");
        I18n.bind(upload.textProperty(),"button.upload");
        I18n.bind(export.textProperty(),"button.export");
        I18n.bind(open.textProperty(),"button.open");
    }

    private void bindTextFlows(TextFlow textFlow, String orientation) {
        for (int i = 0; i < textFlow.getChildren().size(); i++) {
            Text text = (Text) textFlow.getChildren().get(i);
            I18n.bind(text.textProperty(), "start.view." + orientation + ".text" + i);
        }
    }

    private void setListener() {
        this.archive.setOnAction(event -> {
            this.controller.setWorkflow(ARCHIVE);
            stage.openDialog(View.ARCHIVE_DB_DIALOG);
        });
        this.upload.setOnAction(event -> {
            this.controller.setWorkflow(UPLOAD);
            stage.openDialog(View.OPEN_SIARD_ARCHIVE_DIALOG);
        });
        this.export.setOnAction(event -> {
            this.controller.setWorkflow(EXPORT);
            stage.openDialog(View.OPEN_SIARD_ARCHIVE_DIALOG);

        });
        this.open.setOnAction(event -> {
            this.controller.setWorkflow(OPEN);
            stage.openDialog(View.OPEN_SIARD_ARCHIVE_DIALOG);
        });

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
        Line line = new Line(0, bounds.getHeight()/2-10, bounds.getWidth()-25, bounds.getHeight()/2-10);
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
