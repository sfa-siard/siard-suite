package ch.admin.bar.siardsuite.ui;

import ch.admin.bar.siardsuite.framework.dialogs.DialogDisplay;
import ch.admin.bar.siardsuite.framework.errors.HandlingInstruction;
import ch.admin.bar.siardsuite.framework.errors.TypeMatcher;
import ch.admin.bar.siardsuite.framework.errors.WarningDefinition;
import ch.admin.bar.siardsuite.framework.view.ViewDisplay;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKeyArg;
import ch.admin.bar.siardsuite.service.FilesService;
import ch.admin.bar.siardsuite.service.InstallationService;
import ch.admin.bar.siardsuite.service.LogService;
import ch.admin.bar.siardsuite.service.ServicesFacadeBuilder;
import ch.admin.bar.siardsuite.ui.component.Icon;
import ch.admin.bar.siardsuite.ui.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.ui.presenter.RootPresenter;
import ch.enterag.utils.ProgramInfo;
import com.ibm.db2.jcc.am.SqlException;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class RootStage extends Stage implements ViewDisplay, DialogDisplay {

    private static final I18nKeyArg<String> WINDOW_TITLE = I18nKeyArg.of("window.title");

    private final BorderPane rootPane;
    private final BorderPane dialogPane;

    public RootStage() {
        setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        titleProperty().bind(DisplayableText.of(WINDOW_TITLE, ProgramInfo.getProgramInfo().getVersion()).bindable());

        val servicesFacade = new ServicesFacadeBuilder().build(this);

        servicesFacade.errorHandler()
                .registerInstruction(HandlingInstruction.builder()
                        .matcher(TypeMatcher.builder()
                                .exceptionType(CommunicationsException.class)
                                .build())
                        .warningDefinition(WarningDefinition.builder()
                                .title(DisplayableText.of("Unerwarteter Host"))
                                .message(DisplayableText.of("Der angegeben Datenbank-Host ist unbekannt"))
                                .build())
                        .build())
                .registerInstruction(HandlingInstruction.builder()
                        .matcher(TypeMatcher.builder()
                                .exceptionType(SqlException.class)
                                .build())
                        .warningDefinition(WarningDefinition.builder()
                                .title(DisplayableText.of("Unerwarteter Fehler"))
                                .message(DisplayableText.of("Bei der Interaktion mit der Datenbank ist ein unerwarteter Fehler aufgetreten"))
                                .build())
                        .build());
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> servicesFacade.errorHandler().handle(ex));

        rootPane = RootPresenter.load(
                        servicesFacade.dialogs(),
                        servicesFacade.getService(InstallationService.class),
                        servicesFacade.getService(FilesService.class),
                        servicesFacade.getService(LogService.class),
                        servicesFacade.errorHandler(),
                        () -> fireEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST))
                )
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
