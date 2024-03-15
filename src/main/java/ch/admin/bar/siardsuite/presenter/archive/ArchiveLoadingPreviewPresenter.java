package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.component.Spinner;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.framework.general.DbInteractionService;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.ProgressItem;
import ch.admin.bar.siardsuite.presenter.ProgressItems;
import ch.admin.bar.siardsuite.presenter.archive.model.SiardArchiveWithConnectionData;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.ErrorHandler;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.val;

import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.CANCEL;

public class ArchiveLoadingPreviewPresenter {

    private static final I18nKey TITLE = I18nKey.of("archiveLoadingPreview.view.title");
    private static final I18nKey TEXT = I18nKey.of("archiveLoadingPreview.view.text");

    @FXML
    public Label title;
    @FXML
    public Text text;
    @FXML
    public VBox leftVBox;
    @FXML
    public BorderPane borderPane;
    @FXML
    public ImageView loader;
    @FXML
    public VBox scrollBox;
    @FXML
    public MFXProgressBar progressBar;

    private final Image loading = Icon.loading;

    private final ProgressItems progressItems = new ProgressItems();

    public void init(
            final DbmsConnectionData dbmsConnectionData,
            final StepperNavigator<SiardArchiveWithConnectionData> navigator,
            final DbInteractionService dbInteractionService,
            final Dialogs dialogs,
            final ErrorHandler errorHandler
    ) {
        this.title.textProperty().bind(DisplayableText.of(TITLE).bindable());
        this.text.textProperty().bind(DisplayableText.of(TEXT).bindable());

        this.loader.setImage(loading);
        new Spinner(this.loader).play();

        ButtonBox buttonsBox = new ButtonBox().make(CANCEL);
        this.borderPane.setBottom(buttonsBox);

        buttonsBox.previous().setOnAction(event -> {
            navigator.previous();
            dbInteractionService.cancelRunning();
        });
        buttonsBox.cancel().setOnAction(event -> dialogs
                .openDialog(View.ARCHIVE_ABORT_DIALOG));

        dbInteractionService.execute(LoadDatabaseInstruction.builder()
                .connectionData(dbmsConnectionData)
                .loadOnlyMetadata(true)
                .onSuccess(siardArchive -> {
                    navigator.next(SiardArchiveWithConnectionData.builder()
                            .dbmsConnectionData(dbmsConnectionData)
                            .siardArchive(siardArchive)
                            .build());
                })
                .onFailure(event -> {
                    navigator.previous();
                    errorHandler.handle(event.getSource().getException());
                })
                .onProgress((o, oldValue, newValue) -> {
                    progressBar.progressProperty().set(newValue.doubleValue());
                })
                .onSingleValueCompleted((o1, oldValue, newValue) -> {
                    newValue.forEach(p -> addLoadingItem(p.getKey(), new AtomicInteger().getAndIncrement()));
                })
                .build());
    }

    private void addLoadingItem(String text, Integer pos) {
        ObservableList<Node> children = scrollBox.getChildren();
        setPreviousItemToOk(children);
        ProgressItem newItem = new ProgressItem(pos, text);
        progressItems.add(newItem);
        children.add(newItem.icon());
    }

    private void setPreviousItemToOk(ObservableList<Node> children) {
        if (children.size() > 0) {
            ProgressItem previous = this.progressItems.last();
            previous.ok();
        }
    }

    public static LoadedFxml<ArchiveLoadingPreviewPresenter> load(
            final DbmsConnectionData dbmsConnectionData,
            final StepperNavigator<SiardArchiveWithConnectionData> navigator,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<ArchiveLoadingPreviewPresenter>load("fxml/archive/archive-loading-preview.fxml");
        loaded.getController().init(
                dbmsConnectionData,
                navigator,
                servicesFacade.dbInteractionService(),
                servicesFacade.dialogs(),
                servicesFacade.errorHandler()
        );

        return loaded;
    }
}
