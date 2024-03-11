package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.IconView;
import ch.admin.bar.siardsuite.component.LabelIcon;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.presenter.upload.model.ShowUploadResultsData;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.val;

import java.util.Optional;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.FAILED;
import static ch.admin.bar.siardsuite.component.ButtonBox.Type.TO_START;
import static ch.admin.bar.siardsuite.model.View.START;

public class UploadResultPresenter {

    private static final I18nKey FAILED_TITLE = I18nKey.of("upload.result.failed.title");
    private static final I18nKey FAILED_MESSAGE = I18nKey.of("upload.result.failed.message");
    private static final I18nKey SUCCESS_TITLE = I18nKey.of("upload.result.success.title");

    @FXML
    public Label title;
    @FXML
    public Label summary;
    @FXML
    public BorderPane borderPane;
    @FXML
    public Label subtitle1;
    @FXML
    public ScrollPane resultBox;
    @FXML
    public VBox scrollBox;
    @FXML
    public Label errorMessage;
    @FXML
    public TextArea stackTrace;

    public void init(
            final Archive archive,
            final Optional<Failure> uploadFailure,
            final StepperNavigator<Void> stepperNavigator,
            final Navigator navigator
    ) {
        OptionalHelper.ifPresentOrElse(
                uploadFailure,
                failure -> {
                    title.textProperty().bind(DisplayableText.of(FAILED_TITLE).bindable());
                    summary.textProperty().bind(DisplayableText.of(FAILED_MESSAGE).bindable());

                    errorMessage.textProperty().setValue(failure.message());
                    stackTrace.textProperty().setValue(failure.stacktrace());
                    title.getStyleClass().setAll("x-circle-icon", "h2", "label-icon-left");

                    val buttonsBox = new ButtonBox().make(FAILED);
                    buttonsBox.next().setOnAction((event) -> navigator.navigate(START));
                    this.borderPane.setBottom(buttonsBox);
                    buttonsBox.cancel().setOnAction((event) -> {
                        stepperNavigator.previous();
                    });
                },
                () -> {
                    title.textProperty().bind(DisplayableText.of(SUCCESS_TITLE).bindable());

                    this.subtitle1.setVisible(true);
                    this.resultBox.setVisible(true);
                    setResultData(archive);
                    title.getStyleClass().setAll("ok-circle-icon", "h2", "label-icon-left");

                    val buttonsBox = new ButtonBox().make(TO_START);
                    this.borderPane.setBottom(buttonsBox);
                    buttonsBox.next().setOnAction((event) -> navigator.navigate(START));
                    buttonsBox.cancel().setOnAction((event) -> {
                        stepperNavigator.previous();
                    });
                }
        );
    }

    private void setResultData(final Archive archive) {
        this.subtitle1.setText(archive.getMetaData().getDbName());
        long total = 0;
        for (int i = 0; i < archive.getSchemas(); i++) {
            Schema schema = archive.getSchema(i);
            scrollBox.getChildren().add(new Label(schema.getMetaSchema().getName()));
            for (int y = 0; y < schema.getTables(); y++) {
                String tableName = schema.getTable(y).getMetaTable().getName();
                long rows = schema.getTable(y).getMetaTable().getRows();
                addTableData(tableName, rows, y);
                total += rows;
            }
        }
        I18n.bind(summary.textProperty(), "upload.result.success.message", total);
    }

    private void addTableData(String tableName, Long rows, Integer pos) {
        LabelIcon label = new LabelIcon(tableName, pos, IconView.IconType.OK);
        I18n.bind(label.textProperty(), "upload.result.success.table.rows", tableName, rows);
        scrollBox.getChildren().add(label);
    }

    public static LoadedFxml<UploadResultPresenter> load(
            final ShowUploadResultsData data,
            final StepperNavigator<Void> navigator,
            final Archive context,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<UploadResultPresenter>load("fxml/upload/upload-result.fxml");
        loaded.getController().init(
                context,
                data.getFailure(),
                navigator,
                servicesFacade.navigator()
        );

        return loaded;
    }
}
