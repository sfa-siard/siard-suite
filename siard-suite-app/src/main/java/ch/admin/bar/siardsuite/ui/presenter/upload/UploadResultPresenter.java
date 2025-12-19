package ch.admin.bar.siardsuite.ui.presenter.upload;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.ui.component.ButtonBox;
import ch.admin.bar.siardsuite.ui.component.IconView;
import ch.admin.bar.siardsuite.ui.component.LabelIcon;
import ch.admin.bar.siardsuite.ui.presenter.upload.model.ArchiveAdder;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.val;

import java.util.Optional;

import static ch.admin.bar.siardsuite.ui.View.START;
import static ch.admin.bar.siardsuite.ui.component.ButtonBox.Type.TO_START;

public class UploadResultPresenter {

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

    public void init(
            final Archive archive,
            final StepperNavigator<Void> stepperNavigator,
            final Navigator navigator
    ) {
        title.textProperty().bind(DisplayableText.of(SUCCESS_TITLE).bindable());

        this.subtitle1.setVisible(true);
        this.resultBox.setVisible(true);
        setResultData(archive);
        title.getStyleClass().setAll("ok-circle-icon", "h2", "label-icon-left");

        val buttonsBox = new ButtonBox().make(TO_START);
        this.borderPane.setBottom(buttonsBox);
        buttonsBox.next().setOnAction((event) -> navigator.navigate(START));
        buttonsBox.cancel().setOnAction((event) -> stepperNavigator.previous());
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

    public static LoadedView<UploadResultPresenter> load(
            final Archive data,
            final StepperNavigator<Void> navigator,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<UploadResultPresenter>load("fxml/upload/upload-result.fxml");
        loaded.getController().init(
                data,
                navigator,
                servicesFacade.navigator()
        );

        return loaded;
    }
}
