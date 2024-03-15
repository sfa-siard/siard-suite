package ch.admin.bar.siardsuite.presenter.archive.browser;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.scene.Node;
import lombok.val;

import java.io.IOException;
import java.util.Optional;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.OPEN_PREVIEW;

public class OpenArchiveBrowser extends Presenter {
    private static final I18nKey TITLE = I18nKey.of("open.siard.archive.preview.title");
    private static final I18nKey TEXT = I18nKey.of("open.siard.archive.preview.text");

    private LoadedFxml<GenericArchiveBrowserPresenter> loadedFxml;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;
        val dialogs = ServicesFacade.INSTANCE.dialogs(); // TODO
        val navigator = ServicesFacade.INSTANCE.navigator(); // TODO
        val archiveHandler = ServicesFacade.INSTANCE.archiveHandler(); // TODO

        val archiveBrowserView = new TreeBuilder(controller.getSiardArchive(), false);

        val buttonsBox = new ButtonBox().make(OPEN_PREVIEW);
        buttonsBox.cancel().setOnAction(event -> {
            dialogs.openRecentConnectionsDialogForUploading(
                    () -> navigator.navigate(View.UPLOAD_STEPPER),
                    dbConnection -> {
                        controller.setRecentDatabaseConnection(Optional.of(dbConnection));
                        navigator.navigate(View.UPLOAD_STEPPER);
                    }
            );
        });
        buttonsBox.previous().setOnAction(event -> dialogs.openDialog(View.EXPORT_SELECT_TABLES));
        buttonsBox.next().setOnAction(event -> {
            try {
                val archive = controller.getSiardArchive().getArchive();
                archiveHandler.save(archive, archive.getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            navigator.navigate(View.START);
        });

        this.loadedFxml = GenericArchiveBrowserPresenter.load(
                null,
                stage,
                DisplayableText.of(TITLE),
                DisplayableText.of(TEXT),
                buttonsBox,
                archiveBrowserView.createRootItem());
    }

    public Node getView() {
        return loadedFxml.getNode();
    }

    public static LoadedFxml<Presenter> load(
            final Controller controller,
            final RootStage stage
    ) {
        val browser = new OpenArchiveBrowser();
        browser.init(controller, stage);
        return new LoadedFxml<>(browser::getView, browser);
    }
}
