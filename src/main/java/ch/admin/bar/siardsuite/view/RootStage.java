package ch.admin.bar.siardsuite.view;

import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.presenter.RootPresenter;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.atomic.AtomicReference;

public class RootStage extends Stage {

    private final BorderPane rootPane;
    private final BorderPane dialogPane;

    private final AtomicReference<LoadedFxml> previouslyLoadedView = new AtomicReference<>();

    public RootStage() {
        setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        ServicesFacade.INSTANCE.setRootStage(this);

        rootPane = RootPresenter.load(ServicesFacade.INSTANCE, this)
                .getNode();

        dialogPane = DialogPresenter.load()
                .getNode();

        dialogPane.setVisible(false);

        // load start view
        ServicesFacade.INSTANCE.navigator().navigate(View.START);

        // set overall stack pane
        StackPane stackPane = new StackPane(rootPane, dialogPane);

        Scene scene = new Scene(stackPane);
        scene.getRoot().getStyleClass().add("root");
        scene.setFill(null);

        this.setMaximized(true);
        this.initStyle(StageStyle.DECORATED);
        this.getIcons().add(Icon.archiveRed);
        this.setScene(scene);
        this.show();
    }

    public void displayView(final Node node) {
        rootPane.setCenter(node);
    }

    public void displayDialog(final Node node) {
        dialogPane.setCenter(node);
        dialogPane.setVisible(true);
    }

    public void closeDialog() {
        dialogPane.setVisible(false);
    }
}
