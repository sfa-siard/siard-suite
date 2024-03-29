package ch.admin.bar.siardsuite.ui.presenter;

import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.model.Tuple;
import ch.admin.bar.siardsuite.ui.View;
import ch.admin.bar.siardsuite.ui.animations.Animation;
import ch.admin.bar.siardsuite.ui.common.Icon;
import ch.admin.bar.siardsuite.ui.common.Workflow;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;
import lombok.val;

import java.util.Optional;

import static ch.admin.bar.siardsuite.ui.common.Workflow.ARCHIVE;
import static ch.admin.bar.siardsuite.ui.common.Workflow.EXPORT;
import static ch.admin.bar.siardsuite.ui.common.Workflow.OPEN;
import static ch.admin.bar.siardsuite.ui.common.Workflow.UPLOAD;
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

    public void init(
            Optional<Workflow> initWorkflow,
            Dialogs dialogs,
            Navigator navigator
    ) {
        this.dialogs = dialogs;
        this.navigator = navigator;

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
            dbImg.setImage(Icon.SIARD_DB_RED.toImage());
            archiveImg.setImage(Icon.ARCHIVE_RED.toImage());
            animation.start(archiveArrow, archiveBubble);
        });
        archive.setOnMouseExited(event -> resetImageViews());

        upload.setOnMouseEntered(event -> {
            dbRightImg.setImage(Icon.SIARD_DB_RED.toImage());
            archiveImg.setImage(Icon.ARCHIVE_RED.toImage());
            animation.start(uploadArrow, uploadBubble);
        });
        upload.setOnMouseExited(event -> resetImageViews());

        export.setOnMouseEntered(event -> {
            exportImg.setImage(Icon.EXPORT_RED.toImage());
            archiveImg.setImage(Icon.ARCHIVE_RED.toImage());
            animation.start(exportArrow, exportBubble);
        });
        export.setOnMouseExited(event -> resetImageViews());

        open.setOnMouseMoved(event -> archiveImg.setImage(Icon.ARCHIVE_RED.toImage()));
        open.setOnMouseExited(event -> archiveImg.setImage(Icon.ARCHIVE.toImage()));
    }

    private void resetImageViews() {
        animation.stop();
        dbImg.setImage(Icon.SIARD_DB.toImage());
        dbRightImg.setImage(Icon.SIARD_DB.toImage());
        archiveImg.setImage(Icon.ARCHIVE.toImage());
        exportImg.setImage(Icon.EXPORT.toImage());
        archiveBubble.setVisible(false);
        uploadBubble.setVisible(false);
        exportBubble.setVisible(false);
    }

    private void initializeWorkflow(Workflow workflow) {
        switch (workflow) {
            case ARCHIVE:
                dialogs.open(
                        View.RECENT_CONNECTIONS_FOR_ARCHIVING,
                        optionalRecentDbConnection -> navigator.navigate(
                                View.ARCHIVE_STEPPER,
                                optionalRecentDbConnection
                        ));
                break;
            case OPEN:
                dialogs.open(
                        View.OPEN_SIARD_ARCHIVE_DIALOG,
                        (file, archive) -> navigator.navigate(
                                View.OPEN_SIARD_ARCHIVE_PREVIEW,
                                archive
                        ));
                break;
            case EXPORT:
                dialogs.open(
                        View.OPEN_SIARD_ARCHIVE_DIALOG,
                        (file, archive) -> {
                            dialogs.open(
                                    View.EXPORT_SELECT_TABLES,
                                    archive
                            );
                        });
                break;
            case UPLOAD:
                dialogs.open(
                        View.OPEN_SIARD_ARCHIVE_DIALOG,
                        (file, archive) -> dialogs.open(
                                View.RECENT_CONNECTIONS_FOR_UPLOAD,
                                optionalRecentDbConnection -> OptionalHelper.when(optionalRecentDbConnection)
                                        .isPresent(recentDbConnection -> navigator.navigate(
                                                View.UPLOAD_STEPPER_WITH_RECENT_CONNECTION,
                                                new Tuple<>(archive, recentDbConnection)
                                        ))
                                        .orElse(() -> navigator.navigate(
                                                View.UPLOAD_STEPPER,
                                                archive
                                        ))
                        ));
                break;
        }
    }

    public static LoadedView<StartPresenter> load(final ServicesFacade servicesFacade) {
        val loaded = FXMLLoadHelper.<StartPresenter>load("fxml/start.fxml");
        loaded.getController().init(
                Optional.empty(),
                servicesFacade.dialogs(),
                servicesFacade.navigator());

        return loaded;
    }

    public static LoadedView<StartPresenter> load(
            final Workflow initWorkflow,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<StartPresenter>load("fxml/start.fxml");
        loaded.getController().init(
                Optional.of(initWorkflow),
                servicesFacade.dialogs(),
                servicesFacade.navigator());

        return loaded;
    }
}
