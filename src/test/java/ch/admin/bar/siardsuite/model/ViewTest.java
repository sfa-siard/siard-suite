package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.Workflow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewTest {

    @Test
    void getCorrectViewForWorkflow() {
        assertEquals(View.ARCHIVE_DB_DIALOG, View.forWorkflow(Workflow.ARCHIVE));
        assertEquals(View.OPEN_SIARD_ARCHIVE_DIALOG, View.forWorkflow(Workflow.OPEN));
        assertEquals(View.OPEN_SIARD_ARCHIVE_DIALOG, View.forWorkflow(Workflow.UPLOAD));
        assertEquals(View.OPEN_SIARD_ARCHIVE_DIALOG, View.forWorkflow(Workflow.EXPORT));
    }
}