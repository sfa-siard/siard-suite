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
    FORM_RENDERER("fxml/tree/form-renderer.fxml",
            null,
            null,
            true),
    SCHEMA("fxml/tree/table.fxml",
            "tableContainer.title.schema",
            "tableContainer.labelSchema",
            false),
    TABLES("fxml/tree/table.fxml",
            "tableContainer.labelSchema",
            "tableContainer.labelDescSchema",
            false),
    TABLE("fxml/tree/table.fxml",
            "tableContainer.labelTable",
            "tableContainer.labelNumberOfRows",
            true),
    VIEWS("fxml/tree/table.fxml",
            "tableContainer.labelSchema",
            "tableContainer.labelDescSchema",
            false),
    VIEW("fxml/tree/table.fxml",
            "tableContainer.labelView",
            "tableContainer.labelNumberOfRows",
            true),
    COLUMNS("fxml/tree/table.fxml",
            "tableContainer.labelTable",
            "tableContainer.labelNumberOfRows",
            true),
    COLUMN("fxml/tree/column.fxml",
            null,
            null,
            false),
    ROWS("fxml/tree/table.fxml",
            "tableContainer.labelTable",
            "tableContainer.labelNumberOfRows",
            true),
    USERS("fxml/tree/table.fxml",
            "tableContainer.users",
            "tableContainer.labelNumberOfRows",
            false),
    TYPES("fxml/tree/table.fxml",
            "tableContainer.labelSchema",
            "tableContainer.labelDescSchema",
            false),
    TYPE("fxml/tree/type.fxml",
            "tableContainer.labelSchema",
            "tableContainer.labelDescSchema",
            false),
    ATTRIBUTES("fxml/tree/type.fxml",
            "tableContainer.attributes",
            "tableContainer.labelNumerOfRows",
            false),
    ATTRIBUTE("fxml/tree/attribute.fxml",
            null,
            null,
            false),
    PRIVILIGES("fxml/tree/table.fxml",
            "tableContainer.priviliges",
            "tableContainer.labelNumberOfRows",
            false),
    ROUTINES("fxml/tree/table.fxml",
            "tableContainer.labelSchema",
            "tableContainer.labelDescSchema",
            false),
    ROUTINE("fxml/tree/table.fxml",
            "tableContainer.labelRoutine",
            "tableContainer.labelNumberOfRows",
            true),
    PARAMETER("fxml/tree/parameter.fxml",
            null,
            null,
            false);

    private final String viewName;
    private final String nameLabel;
    private final String infoLabel;
    private final Boolean hasTableSearch;

    TreeContentView(String viewName, String nameLabel, String infoLabel, Boolean hasTableSearch) {
        this.viewName = viewName;
        this.nameLabel = nameLabel;
        this.infoLabel = infoLabel;
        this.hasTableSearch = hasTableSearch;
    }

    public String getViewName() {
        return viewName;
    }

    public String getNameLabel() {
        return nameLabel;
    }

    public String getInfoLabel() {
        return infoLabel;
    }

    public Boolean getHasTableSearch() {
        return hasTableSearch;
    }
}
