package ch.admin.bar.siardsuite.presenter.open;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.presenter.ArchiveBrowserPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.OPEN_PREVIEW;

public class OpenPreviewPresenter extends ArchiveBrowserPresenter {

    @FXML
    public VBox container;
    @FXML
    public VBox leftTreeBox;

    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected ButtonBox buttonsBox;
    @FXML
    public Label titleTableContainer;


    @Override
    public void init(Controller controller, RootStage stage) {
        super.init(controller, stage);

        I18n.bind(title.textProperty(), "open.siard.archive.preview.title");
        I18n.bind(text.textProperty(), "open.siard.archive.preview.text");

        initButtons();
    }

    private void initButtons() {
        this.buttonsBox = new ButtonBox().make(OPEN_PREVIEW);
        this.borderPane.setBottom(buttonsBox);
        buttonsBox.cancel().setOnAction(event -> this.controller.initializeUpload(stage));
        buttonsBox.previous().setOnAction(event -> this.controller.initializeExport(stage));
        buttonsBox.next().setOnAction(event -> this.controller.start(stage));
    }
}
