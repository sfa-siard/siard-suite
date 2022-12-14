package ch.admin.bar.siardsuite.model;

public enum TreeContentView {

  ROOT("fxml/tree/content-root.fxml", "tableContainer.title.siardFile", null, null, false),
  SCHEMAS("fxml/tree/content-root.fxml", "tableContainer.title.schemas", null, null, false),
  SCHEMA("fxml/tree/table.fxml", "tableContainer.title.schema", "tableContainer.labelSchema", "tableContainer.labelDescSchema", false),
  TABLES("fxml/tree/table.fxml", "tableContainer.title.tables", "tableContainer.labelSchema", "tableContainer.labelDescSchema", false),
  TABLE("fxml/tree/table.fxml", "tableContainer.title.table", "tableContainer.labelTable", "tableContainer.labelNumberOfRows", true),
  COLUMNS("fxml/tree/table.fxml", "tableContainer.title.columns", "tableContainer.labelTable", "tableContainer.labelNumberOfRows", true),
  COLUMN("fxml/tree/column.fxml", "tableContainer.title.column", null, null, false),
  ROWS("fxml/tree/table.fxml", "tableContainer.title.data", "tableContainer.labelTable", "tableContainer.labelNumberOfRows", true);

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
