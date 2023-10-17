package ch.admin.bar.siardsuite.model;

/**
 * Enumeration to define the available content views when browsing a SIARD archive.
 * Each value configures the content view for a selection in the tree view, e.g. if the
 * user select a specific table, or a node like Routines
 */
public enum TreeContentView {

    /**
     * the content for the root of the tree
     */
    FORM_RENDERER("fxml/tree/form-renderer.fxml");

    private final String viewName;

    TreeContentView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }
}
