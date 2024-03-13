package ch.admin.bar.siardsuite.framework.navigation;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.Workflow;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.view.RootStage;
import lombok.RequiredArgsConstructor;

/**
 * Navigator class for controlling navigation within the application.
 */
@RequiredArgsConstructor
public class Navigator {

    private final Controller controller;
    private final RootStage stage;

    /**
     * Initializes the specified workflow.
     */
    public void initializeWorkflow(Workflow workflow) {
        controller.initializeWorkflow(workflow, stage);
    }

    /**
     * Opens the specified SIARD archive in a browser view.
     */
    public void openArchive(Archive archive) {
        controller.setSiardArchive(archive.getFile().getName(), archive);
        stage.navigate(View.OPEN_SIARD_ARCHIVE_PREVIEW);
    }

    /**
     * Navigates to the specified view.
     */
    public void navigate(View view) {
        stage.navigate(view);
    }
}
