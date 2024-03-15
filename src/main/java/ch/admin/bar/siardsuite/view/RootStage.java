package ch.admin.bar.siardsuite.view;

import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.framework.DialogDisplay;
import ch.admin.bar.siardsuite.framework.ViewDisplay;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.presenter.RootPresenter;
import ch.admin.bar.siardsuite.service.ServicesFacadeBuilder;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.val;

public class RootStage extends Stage implements ViewDisplay, DialogDisplay {

    private final BorderPane rootPane;
    private final BorderPane dialogPane;

    public RootStage() {
        setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        val servicesFacade = new ServicesFacadeBuilder().build(this);

        rootPane = RootPresenter.load(servicesFacade, this)
                .getNode();

        dialogPane = DialogPresenter.load()
                .getNode();

        dialogPane.setVisible(false);

        // load start view
        servicesFacade.navigator()
                .navigate(View.START);

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

    @Override
    public void displayView(final Node node) {
        rootPane.setCenter(node);
    }

    @Override
    public void displayDialog(final Node node) {
        dialogPane.setCenter(node);
        dialogPane.setVisible(true);
    }

    @Override
    public void closeDialog() {
        dialogPane.setVisible(false);
    }
}
