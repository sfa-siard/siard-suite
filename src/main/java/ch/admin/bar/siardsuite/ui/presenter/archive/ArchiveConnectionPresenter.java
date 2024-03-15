package ch.admin.bar.siardsuite.ui.presenter.archive;

import ch.admin.bar.siardsuite.ui.component.ButtonBox;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.service.database.model.Dbms;
import ch.admin.bar.siardsuite.service.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.service.database.model.DbmsConnectionProperties;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.ui.presenter.archive.model.DbmsWithInitialValue;
import ch.admin.bar.siardsuite.ui.presenter.connection.ConnectionForm;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.service.preferences.RecentDbConnection;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import lombok.val;

import java.util.Optional;

import static ch.admin.bar.siardsuite.ui.component.ButtonBox.Type.DEFAULT;

public class ArchiveConnectionPresenter {

    @FXML
    public BorderPane borderPane;

    @FXML
    private ConnectionForm connectionForm;

    public void init(
            final Dbms dbms,
            final Optional<RecentDbConnection> initialValue,
            final StepperNavigator<DbmsConnectionData> stepperNavigator,
            final Dialogs dialogs
    ) {
        val buttonsBox = new ButtonBox().make(DEFAULT);
        borderPane.setBottom(buttonsBox);

        connectionForm.show(dbms);

        initialValue.ifPresent(recentDbConnection -> {
            val dbmsConnectionData = recentDbConnection.mapToDbmsConnectionData();
            final DbmsConnectionProperties properties = dbmsConnectionData.getProperties();

            connectionForm.show(dbms, properties, recentDbConnection.getName());
        });

        buttonsBox.next()
                .setOnAction((event) -> connectionForm
                        .tryGetValidConnectionData()
                        .ifPresent(stepperNavigator::next));

        buttonsBox.previous().setOnAction((event) -> stepperNavigator.previous());
        buttonsBox.cancel().setOnAction((event) -> dialogs
                .open(View.ARCHIVE_ABORT_DIALOG));
    }

    public static LoadedView<ArchiveConnectionPresenter> load(
            final DbmsWithInitialValue dbmsSelected,
            final StepperNavigator<DbmsConnectionData> navigator,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<ArchiveConnectionPresenter>load("fxml/archive/archive-connection.fxml");
        loaded.getController().init(
                dbmsSelected.getDbms(),
                dbmsSelected.getInitialValue(),
                navigator,
                servicesFacade.dialogs()
        );

        return loaded;
    }
}
