package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionProperties;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.presenter.connection.ConnectionForm;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import lombok.val;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;

public class ArchiveConnectionPresenter extends StepperPresenter {

    @FXML
    public BorderPane borderPane;

    @FXML
    private ButtonBox buttonsBox;

    @FXML
    private ConnectionForm connectionForm;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, RootStage stage, MFXStepper stepper) {
        this.init(controller, stage);

        buttonsBox = new ButtonBox().make(DEFAULT);
        borderPane.setBottom(buttonsBox);

        setListeners(stepper);
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.RECENT_CONNECTION_SELECTED_EVENT, event -> {
            val recentConnection = event.getRecentConnectionData();

            final Dbms dbms = recentConnection.mapToDbmsConnectionData().getDbms();
            final DbmsConnectionProperties properties = recentConnection.mapToDbmsConnectionData().getProperties();

            connectionForm.show(dbms, properties, recentConnection.getName());
        });

        stepper.addEventHandler(SiardEvent.ARCHIVE_DBMS_SELECTED, event -> {
            connectionForm.show(event.getSelectedDbms());
        });

        buttonsBox.next().setOnAction((event) -> {
            connectionForm.tryGetValidConnectionData()
                    .ifPresent(dbmsConnectionData -> {
                        stepper.next();
                        stepper.fireEvent(new SiardEvent.DbmsConnectionDataReadyEvent(SiardEvent.ARCHIVE_CONNECTION_DATA_READY, dbmsConnectionData));
                    });
        });

        this.buttonsBox.previous().setOnAction((event) -> stepper.previous());
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
    }
}
