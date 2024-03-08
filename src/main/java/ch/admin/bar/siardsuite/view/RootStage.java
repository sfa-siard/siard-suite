package ch.admin.bar.siardsuite.view;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.component.rendering.TreeItemsExplorer;
import ch.admin.bar.siardsuite.framework.general.Dialogs;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.presenter.ErrorDialogPresenter;
import ch.admin.bar.siardsuite.presenter.archive.dialogs.ArchiveRecentConnectionsDialogPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.dialogues.UnsavedChangesDialogPresenter;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.archive.browser.dialogues.SearchMetadataDialogPresenter;
import ch.admin.bar.siardsuite.presenter.archive.browser.dialogues.SearchTableDialogPresenter;
import ch.admin.bar.siardsuite.presenter.open.OpenSiardArchiveDialogPresenter;
import ch.admin.bar.siardsuite.util.preferences.RecentDbConnection;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RootStage extends Stage implements ErrorHandler, Dialogs {
  private final Controller controller;

  private final BorderPane rootPane;
  private final BorderPane dialogPane;

  public RootStage(Controller controller) throws IOException {
    this.controller = controller;

    rootPane = View.ROOT.getViewCreator()
                    .apply(controller, this)
                    .getNode();
    dialogPane = View.DIALOG.getViewCreator()
            .apply(controller, this)
            .getNode();
    dialogPane.setVisible(false);

    // load start view
    navigate(View.START);

    // set overall stack pane
    StackPane stackPane = new StackPane(rootPane, dialogPane);

    Scene scene = new Scene(stackPane);
    scene.getRoot().getStyleClass().add("root");
    scene.setFill(null);

    this.setMaximized(true);
    this.initStyle(StageStyle.DECORATED);
    this.getIcons().add(Icon.archiveRed);
    this.setScene(scene);
    this.show();
  }

  private void setCenter(BorderPane borderPane, View view) {
    val loaded = view.getViewCreator().apply(controller, this);
    borderPane.setCenter(loaded.getNode());
  }

  public void navigate(View view) {
    setCenter(rootPane, view);
  }

  @Override
  public void openDialog(View view) {
    setCenter(dialogPane, view);
    dialogPane.setVisible(true);
  }

  @Override
  public void handle(final Throwable e) {
    val loaded = ErrorDialogPresenter.load(new Failure(e), this::closeDialog);

    dialogPane.setCenter(loaded.getNode());
    dialogPane.setVisible(true);
  }

  @Override
  public void openUnsavedChangesDialog(final Consumer<UnsavedChangesDialogPresenter.Result> resultCallback) {
    val loaded = UnsavedChangesDialogPresenter.load(result -> {
      closeDialog();
      resultCallback.accept(result);
    });

    dialogPane.setCenter(loaded.getNode());
    dialogPane.setVisible(true);
  }

  @Override
  public void openSearchTableDialog(final Consumer<Optional<String>> searchTermConsumer) {
    val loaded = SearchTableDialogPresenter.load(this::closeDialog, searchTermConsumer);

    dialogPane.setCenter(loaded.getNode());
    dialogPane.setVisible(true);
  }

  @Override
  public void openSearchMetaDataDialog(
          final TreeItemsExplorer treeItemsExplorer,
          final Consumer<TreeItem<TreeAttributeWrapper>> onSelected) {
    val loaded = SearchMetadataDialogPresenter.load(
            this::closeDialog,
            treeItemsExplorer,
            onSelected
    );

    dialogPane.setCenter(loaded.getNode());
    dialogPane.setVisible(true);
  }

  @Override
  public void openRecentConnectionsDialogForArchiving(
          final Runnable onNewConnection,
          final Consumer<RecentDbConnection> onRecentConnectionSelected
  ) {
    val loaded = ArchiveRecentConnectionsDialogPresenter.loadForArchiving(
            this::closeDialog,
            onNewConnection,
            onRecentConnectionSelected
    );

    dialogPane.setCenter(loaded.getNode());
    dialogPane.setVisible(true);
  }

  @Override
  public void openRecentConnectionsDialogForUploading(
          final Runnable onNewConnection,
          final Consumer<RecentDbConnection> onRecentConnectionSelected
  ) {
    val loaded = ArchiveRecentConnectionsDialogPresenter.loadForUpload(
            this::closeDialog,
            onNewConnection,
            onRecentConnectionSelected
    );

    dialogPane.setCenter(loaded.getNode());
    dialogPane.setVisible(true);
  }

  @Override
  public void openSelectSiardFileDialog(final BiConsumer<File, Archive> onArchiveSelected) {
    val loaded = OpenSiardArchiveDialogPresenter.load(
            this::closeDialog,
            this,
            onArchiveSelected
    );

    dialogPane.setCenter(loaded.getNode());
    dialogPane.setVisible(true);
  }

  public void closeDialog() {
    dialogPane.setVisible(false);
  }
}
