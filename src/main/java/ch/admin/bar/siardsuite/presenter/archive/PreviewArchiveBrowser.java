package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.archive.browser.GenericArchiveBrowserPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.TreeBuilder;
import ch.admin.bar.siardsuite.presenter.archive.model.SiardArchiveWithConnectionData;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.scene.Node;
import lombok.val;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;

public class PreviewArchiveBrowser {

    private static final I18nKey TITLE = I18nKey.of("archivePreview.view.title");
    private static final I18nKey TEXT = I18nKey.of("archivePreview.view.text");

    private ButtonBox buttonsBox;
    private LoadedFxml<GenericArchiveBrowserPresenter> loadedFxml;

    public Node getView() {
        return loadedFxml.getNode();
    }

    public void init(
            final SiardArchiveWithConnectionData siardArchive,
            final StepperNavigator<SiardArchiveWithConnectionData> navigator,
            final ServicesFacade servicesFacade
    ) {
        val archiveBrowserView = new TreeBuilder(siardArchive.getSiardArchive(), true);

        this.buttonsBox = new ButtonBox().make(DEFAULT);
        buttonsBox.next().setOnAction((event) -> navigator.next(siardArchive));
        buttonsBox.previous().setOnAction((event) -> navigator.previous());
        buttonsBox.cancel().setOnAction((event) -> servicesFacade
                .dialogs()
                .openDialog(View.ARCHIVE_ABORT_DIALOG));


        this.loadedFxml = GenericArchiveBrowserPresenter.load(
                servicesFacade.dialogs(),
                servicesFacade.errorHandler(),
                DisplayableText.of(TITLE),
                DisplayableText.of(TEXT),
                this.buttonsBox,
                archiveBrowserView.createRootItem());
    }

    public static LoadedFxml<PreviewArchiveBrowser> load(
            final SiardArchiveWithConnectionData siardArchive,
            final StepperNavigator<SiardArchiveWithConnectionData> navigator,
            final ServicesFacade servicesFacade
    ) {
        val browser = new PreviewArchiveBrowser();
        browser.init(siardArchive, navigator, servicesFacade);
        return new LoadedFxml<>(browser::getView, browser);
    }
}
