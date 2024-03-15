package ch.admin.bar.siardsuite.ui.presenter.archive.browser;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.ui.component.ButtonBox;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.navigation.Navigator;
import ch.admin.bar.siardsuite.model.Tuple;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.service.ArchiveHandler;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.framework.ErrorHandler;
import javafx.scene.Node;
import lombok.val;

import java.io.IOException;

import static ch.admin.bar.siardsuite.ui.component.ButtonBox.Type.OPEN_PREVIEW;

public class OpenArchiveBrowser {
    private static final I18nKey TITLE = I18nKey.of("open.siard.archive.preview.title");
    private static final I18nKey TEXT = I18nKey.of("open.siard.archive.preview.text");

    private LoadedView<GenericArchiveBrowserPresenter> loadedView;

    public void init(
            final Archive archive,
            final Dialogs dialogs,
            final Navigator navigator,
            final ErrorHandler errorHandler,
            final ArchiveHandler archiveHandler
    ) {
        val archiveBrowserView = new TreeBuilder(new SiardArchive(archive.getFile().getName(), archive, false), false);

        val buttonsBox = new ButtonBox().make(OPEN_PREVIEW);
        buttonsBox.cancel().setOnAction(event -> dialogs.open(
                View.RECENT_CONNECTIONS_FOR_UPLOAD,
                optionalRecentDbConnection -> OptionalHelper.when(optionalRecentDbConnection)
                        .isPresent(recentDbConnection -> navigator.navigate(
                                View.UPLOAD_STEPPER_WITH_RECENT_CONNECTION,
                                new Tuple<>(
                                        archive,
                                        recentDbConnection
                                )))
                        .orElse(() -> navigator.navigate(
                                View.UPLOAD_STEPPER,
                                archive))

        ));
        buttonsBox.previous().setOnAction(event -> dialogs.open(
                View.EXPORT_SELECT_TABLES,
                archive
        ));
        buttonsBox.next().setOnAction(event -> {
            try {
                archiveHandler.save(archive, archive.getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            navigator.navigate(View.START);
        });

        this.loadedView = GenericArchiveBrowserPresenter.load(
                dialogs,
                errorHandler,
                DisplayableText.of(TITLE),
                DisplayableText.of(TEXT),
                buttonsBox,
                archiveBrowserView.createRootItem());
    }

    public Node getView() {
        return loadedView.getNode();
    }

    public static LoadedView<OpenArchiveBrowser> load(
            final Archive data,
            final ServicesFacade servicesFacade
    ) {
        val browser = new OpenArchiveBrowser();
        browser.init(
                data,
                servicesFacade.dialogs(),
                servicesFacade.navigator(),
                servicesFacade.errorHandler(),
                servicesFacade.getService(ArchiveHandler.class));
        return new LoadedView<>(browser::getView, browser);
    }
}
