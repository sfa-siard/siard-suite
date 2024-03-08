package ch.admin.bar.siardsuite.framework.navigation;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.Workflow;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.view.RootStage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Navigator {

    private final Controller controller;
    private final RootStage stage;

    public void initializeWorkflow(Workflow workflow) {
        controller.initializeWorkflow(workflow, stage);
    }

    public void openArchive(Archive archive) {
        // TODO: Checkm is archive.getFile().getName() same like UserDefinedMetadata.getSaveAt().getName() ?
        controller.setSiardArchive(archive.getFile().getName(), archive);
        stage.navigate(View.OPEN_SIARD_ARCHIVE_PREVIEW);
    }

    public void navigate(View view) {
        stage.navigate(view);
    }
}
