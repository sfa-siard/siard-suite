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
    ROOT("fxml/tree/content-root.fxml", "tableContainer.title.siardFile", null, null, false),
    SCHEMAS("fxml/tree/content-root.fxml", "tableContainer.title.schemas", null, null, false),
    SCHEMA("fxml/tree/table.fxml",
           "tableContainer.title.schema",
           "tableContainer.labelSchema",
           "tableContainer.labelDescSchema",
           false),
    TABLES("fxml/tree/table.fxml",
           "tableContainer.title.tables",
           "tableContainer.labelSchema",
           "tableContainer.labelDescSchema",
           false),
    TABLE("fxml/tree/table.fxml",
          "tableContainer.title.table",
          "tableContainer.labelTable",
          "tableContainer.labelNumberOfRows",
          true),
    VIEWS("fxml/tree/table.fxml",
          "tableContainer.title.views",
          "tableContainer.labelSchema",
          "tableContainer.labelDescSchema",
          false),
    VIEW("fxml/tree/table.fxml",
         "tableContainer.title.view",
         "tableContainer.labelView",
         "tableContainer.labelNumberOfRows",
         true),
    COLUMNS("fxml/tree/table.fxml",
            "tableContainer.title.columns",
            "tableContainer.labelTable",
            "tableContainer.labelNumberOfRows",
            true),
    COLUMN("fxml/tree/column.fxml", "tableContainer.title.column", null, null, false),
    ROWS("fxml/tree/table.fxml",
         "tableContainer.title.data",
         "tableContainer.labelTable",
         "tableContainer.labelNumberOfRows",
         true),
    USERS("fxml/tree/table.fxml",
          "tableContainer.title.users",
          "tableContainer.users",
          "tableContainer.labelNumberOfRows",
          false),
    TYPES("fxml/tree/table.fxml",
          "tableContainer.title.types",
          "tableContainer.types",
          "tableContainer.labelNumberOfRows",
          false),
    TYPE("fxml/tree/table.fxml",
         "tableContainer.title.type",
         "tableContainer.labelSchema",
         "tableContainer.labelDescSchema",
         false),
    ATTRIBUTES("fxml/tree/table.fxml",
               "tableContainer.title.attributes",
               "tableContainer.attributes",
               "tableContainer.labelNumerOfRows",
               false),
    PRIVILIGES("fxml/tree/table.fxml",
               "tableContainer.title.priviliges",
               "tableContainer.priviliges",
               "tableContainer.labelNumberOfRows",
               false),
    ROUTINES("fxml/tree/table.fxml",
             "tableContainer.title.routines",
             "tableContainer.labelSchema",
             "tableContainer.labelDescSchema",
             false),
    ROUTINE("fxml/tree/table.fxml",
            "tableContainer.title.routine",
            "tableContainer.labelRoutine",
            "tableContainer.labelNumberOfRows", true),
    PARAMETER("fxml/tree/parameter.fxml",
              "tableContainer.title.parameter",
              null,
              null,
              false);

    private final String viewName;
    private final String viewTitle;
    private final String nameLabel;
    private final String infoLabel;
    private final Boolean hasTableSearch;

    TreeContentView(String viewName, String viewTitle, String nameLabel, String infoLabel, Boolean hasTableSearch) {
        this.viewName = viewName;
        this.viewTitle = viewTitle;
        this.nameLabel = nameLabel;
        this.infoLabel = infoLabel;
        this.hasTableSearch = hasTableSearch;
    }

    public String getViewName() {
        return viewName;
    }

    public String getViewTitle() {
        return viewTitle;
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
