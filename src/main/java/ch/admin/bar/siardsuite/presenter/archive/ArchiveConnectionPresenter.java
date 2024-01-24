package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionProperties;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.presenter.connection.ConnectionForm;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.val;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;

public class ArchiveConnectionPresenter extends StepperPresenter {

    private static final I18nKey TITLE = I18nKey.of("connection.view.title");

    private static final I18nKey TEXT_0 = I18nKey.of("connection.view.text0");
    private static final I18nKey TEXT_1 = I18nKey.of("connection.view.text1");
    private static final I18nKey TEXT_2 = I18nKey.of("connection.view.text2");
    private static final I18nKey TEXT_3 = I18nKey.of("connection.view.text3");
    private static final I18nKey TEXT_4 = I18nKey.of("connection.view.text4");

    @FXML
    public Text title;
    @FXML
    public Text text0;
    @FXML
    public Text text1;
    @FXML
    public Text text2;
    @FXML
    public Text text3;
    @FXML
    public Text text4;
    @FXML
    public TextFlow textFlow;

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

        title.textProperty().bind(DisplayableText.of(TITLE).bindable());

        text0.textProperty().bind(DisplayableText.of(TEXT_0).bindable());
        text1.textProperty().bind(DisplayableText.of(TEXT_1).bindable());
        text2.textProperty().bind(DisplayableText.of(TEXT_2).bindable());
        text3.textProperty().bind(DisplayableText.of(TEXT_3).bindable());
        text4.textProperty().bind(DisplayableText.of(TEXT_4).bindable());

        text1.getStyleClass().add("bold");
        text3.getStyleClass().add("bold");

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
