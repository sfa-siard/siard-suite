package ch.admin.bar.siardsuite.ui.presenter.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.ui.component.ButtonBox;
import ch.admin.bar.siardsuite.framework.ErrorHandler;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.framework.dialogs.Dialogs;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.model.Tuple;
import ch.admin.bar.siardsuite.ui.View;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.GenericArchiveBrowserPresenter;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.TreeBuilder;
import ch.admin.bar.siardsuite.service.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import javafx.scene.Node;
import lombok.val;

import static ch.admin.bar.siardsuite.ui.component.ButtonBox.Type.DEFAULT;

public class PreviewArchiveBrowser {

    private static final I18nKey TITLE = I18nKey.of("archivePreview.view.title");
    private static final I18nKey TEXT = I18nKey.of("archivePreview.view.text");

    private ButtonBox buttonsBox;
    private LoadedView<GenericArchiveBrowserPresenter> loadedView;

    public Node getView() {
        return loadedView.getNode();
    }

    public void init(
            final Archive archive,
            final DbmsConnectionData connectionData,
            final StepperNavigator<Tuple<Archive, DbmsConnectionData>> navigator,
            final Dialogs dialogs,
            final ErrorHandler errorHandler
    ) {
        val archiveBrowserView = new TreeBuilder(new SiardArchive("", archive, true), true);

        this.buttonsBox = new ButtonBox().make(DEFAULT);
        buttonsBox.next().setOnAction((event) -> navigator.next(new Tuple<>(archive, connectionData)));
        buttonsBox.previous().setOnAction((event) -> navigator.previous());
        buttonsBox.cancel().setOnAction((event) -> dialogs
                .open(View.ARCHIVE_ABORT_DIALOG));


        this.loadedView = GenericArchiveBrowserPresenter.load(
                dialogs,
                errorHandler,
                DisplayableText.of(TITLE),
                DisplayableText.of(TEXT),
                this.buttonsBox,
                archiveBrowserView.createRootItem());
    }

    public static LoadedView<PreviewArchiveBrowser> load(
            final Tuple<Archive, DbmsConnectionData> data,
            final StepperNavigator<Tuple<Archive, DbmsConnectionData>> navigator,
            final ServicesFacade servicesFacade
    ) {
        val browser = new PreviewArchiveBrowser();
        browser.init(
                data.getValue1(),
                data.getValue2(),
                navigator,
                servicesFacade.dialogs(),
                servicesFacade.errorHandler()
        );
        return new LoadedView<>(browser::getView, browser);
    }
}
