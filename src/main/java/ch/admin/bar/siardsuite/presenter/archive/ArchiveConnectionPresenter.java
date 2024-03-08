package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.connection.ConnectionForm;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import lombok.val;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;

public class ArchiveConnectionPresenter {

    @FXML
    public BorderPane borderPane;

    @FXML
    private ConnectionForm connectionForm;

    public void init(
            final Dbms dbms,
            final StepperNavigator<DbmsConnectionData> navigator,
            final ServicesFacade servicesFacade
    ) {
        val buttonsBox = new ButtonBox().make(DEFAULT);
        borderPane.setBottom(buttonsBox);

        connectionForm.show(dbms);

        // TODO FIXME recent connection --> probably customized input data
//        stepper.addEventHandler(SiardEvent.RECENT_CONNECTION_SELECTED_EVENT, event -> {
//            val recentConnection = event.getRecentConnectionData();
//
//            final Dbms dbms = recentConnection.mapToDbmsConnectionData().getDbms();
//            final DbmsConnectionProperties properties = recentConnection.mapToDbmsConnectionData().getProperties();
//
//            connectionForm.show(dbms, properties, recentConnection.getName());
//        });

        buttonsBox.next()
                .setOnAction((event) -> connectionForm
                        .tryGetValidConnectionData()
                        .ifPresent(navigator::next));

        buttonsBox.previous().setOnAction((event) -> navigator.previous());
        buttonsBox.cancel().setOnAction((event) -> servicesFacade
                .dialogs()
                .openDialog(View.ARCHIVE_ABORT_DIALOG));
    }

    public static LoadedFxml<ArchiveConnectionPresenter> load(
            final Dbms dbmsSelected,
            final StepperNavigator<DbmsConnectionData> navigator,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<ArchiveConnectionPresenter>load("fxml/archive/archive-connection.fxml");
        loaded.getController().init(dbmsSelected, navigator, servicesFacade);

        return loaded;
    }
}
