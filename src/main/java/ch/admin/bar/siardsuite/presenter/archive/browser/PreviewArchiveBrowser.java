package ch.admin.bar.siardsuite.presenter.archive.browser;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.TreeBuilder;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.presenter.StepperDependant;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.scene.Node;
import lombok.val;

import java.util.function.BiFunction;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;

public class PreviewArchiveBrowser extends Presenter implements StepperDependant {
    public static final BiFunction<Controller, RootStage, LoadedFxml<Presenter>> VIEW_CREATOR = (controller, rootStage) -> {
        val browser = new PreviewArchiveBrowser();
        browser.init(controller, rootStage);
        return new LoadedFxml<>(browser::getView, browser);
    };

    private static final I18nKey TITLE = I18nKey.of("archivePreview.view.title");
    private static final I18nKey TEXT = I18nKey.of("archivePreview.view.text");

    private ButtonBox buttonsBox;
    private LoadedFxml<GenericArchiveBrowserPresenter> loadedFxml;

    public Node getView() {
        return loadedFxml.getNode();
    }

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;
        val archiveBrowserView = new TreeBuilder(controller.getSiardArchive(), true);

        this.buttonsBox = new ButtonBox().make(DEFAULT);
        this.loadedFxml = GenericArchiveBrowserPresenter.load(
                stage,
                controller,
                DisplayableText.of(TITLE),
                DisplayableText.of(TEXT),
                this.buttonsBox,
                archiveBrowserView.createRootItem());
    }

    @Override
    public void injectStepper(MFXStepper stepper) {
        buttonsBox.next().setOnAction((event) -> stepper.next());
        buttonsBox.previous().setOnAction((event) -> {
            stepper.previous(); // Step over hidden loading step
            stepper.previous();
        });
        buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
    }
}
