package ch.admin.bar.siardsuite.presenter.archive.browser;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ArchiveBrowserView;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.util.I18nKey;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.scene.Node;
import lombok.val;

import java.io.IOException;
import java.util.function.BiFunction;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.OPEN_PREVIEW;

public class OpenArchiveBrowser extends Presenter {
    public static final BiFunction<Controller, RootStage, LoadedFxml<Presenter>> VIEW_CREATOR = (controller, rootStage) -> {
        val browser = new OpenArchiveBrowser();
        browser.init(controller, rootStage);
        return new LoadedFxml<>(browser::getView, browser);
    };

    private static final I18nKey TITLE = I18nKey.of("open.siard.archive.preview.title");
    private static final I18nKey TEXT = I18nKey.of("open.siard.archive.preview.text");

    private ButtonBox buttonsBox;
    private LoadedFxml<GenericArchiveBrowserPresenter> loadedFxml;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;
        val archiveBrowserView = new ArchiveBrowserView(controller.getSiardArchive(), false);

        this.buttonsBox = new ButtonBox().make(OPEN_PREVIEW);
        buttonsBox.cancel().setOnAction(event -> this.controller.initializeUpload(stage));
        buttonsBox.previous().setOnAction(event -> this.controller.initializeExport(stage));
        buttonsBox.next().setOnAction(event -> {
            try {
                this.controller.saveArchiveOnlyMetaData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.controller.start(stage);
        });

        this.loadedFxml = GenericArchiveBrowserPresenter.load(
                stage,
                controller,
                TITLE,
                TEXT,
                this.buttonsBox,
                archiveBrowserView.createRootItem());
    }

    public Node getView() {
        return loadedFxml.getNode();
    }
}
