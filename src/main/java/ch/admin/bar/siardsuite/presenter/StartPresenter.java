package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.Workflow;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.animations.Animation;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;
import lombok.val;

import java.util.Optional;

import static ch.admin.bar.siardsuite.Workflow.ARCHIVE;
import static ch.admin.bar.siardsuite.Workflow.EXPORT;
import static ch.admin.bar.siardsuite.Workflow.OPEN;
import static ch.admin.bar.siardsuite.Workflow.UPLOAD;
import static ch.admin.bar.siardsuite.util.I18n.bind;

public class StartPresenter {

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

    private Dialogs dialogs;
    private Navigator navigator;
    private Controller controller; // FIXME temporary needed

    public void init(
            Optional<Workflow> initWorkflow,
            Dialogs dialogs,
            Navigator navigator,
            Controller controller // FIXME temp
    ) {
        this.dialogs = dialogs;
        this.navigator = navigator;
        this.controller = controller;

        controller.clearSiardArchive();

        resetImageViews();
        setListener();
        bindLabels();

        initWorkflow.ifPresent(this::initializeWorkflow);
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
        this.archive.setOnAction(event -> initializeWorkflow(ARCHIVE));
        this.upload.setOnAction(event -> initializeWorkflow(UPLOAD));
        this.export.setOnAction(event -> initializeWorkflow(EXPORT));
        this.open.setOnAction(event -> initializeWorkflow(OPEN));

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

    private void initializeWorkflow(Workflow workflow) {
        controller.setRecentDatabaseConnection(Optional.empty());

        switch (workflow) {
            case ARCHIVE:
                dialogs.openRecentConnectionsDialogForArchiving(
                        () -> navigator.navigate(View.ARCHIVE_STEPPER),
                        dbConnection -> {
                            controller.setRecentDatabaseConnection(Optional.of(dbConnection));
                            navigator.navigate(View.ARCHIVE_STEPPER);
                        }
                );
                break;
            case OPEN:
                dialogs.openSelectSiardFileDialog((file, archive) -> {
                            controller.setSiardArchive(file.getName(), archive);
                            navigator.navigate(View.OPEN_SIARD_ARCHIVE_PREVIEW);
                        }
                );
                break;
            case EXPORT:
                dialogs.openSelectSiardFileDialog((file, archive) -> {
                            controller.setSiardArchive(file.getName(), archive);
                            dialogs.openDialog(View.EXPORT_SELECT_TABLES);
                        }
                );
                break;
            case UPLOAD:
                dialogs.openSelectSiardFileDialog((file, archive) -> {
                            controller.setSiardArchive(file.getName(), archive);
                            dialogs.openRecentConnectionsDialogForUploading(
                                    () -> navigator.navigate(View.UPLOAD_STEPPER),
                                    dbConnection -> {
                                        controller.setRecentDatabaseConnection(Optional.of(dbConnection));
                                        navigator.navigate(View.UPLOAD_STEPPER);
                                    }
                            );
                        }
                );
                break;
        }
    }

    public static LoadedFxml<StartPresenter> load(
            final Optional<Workflow> initWorkflow,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<StartPresenter>load("fxml/start.fxml");
        loaded.getController().init(
                initWorkflow,
                servicesFacade.dialogs(),
                servicesFacade.navigator(),
                servicesFacade.controller());

        return loaded;
    }
}
