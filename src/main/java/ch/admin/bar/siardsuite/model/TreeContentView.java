package ch.admin.bar.siardsuite.model;

public enum TreeContentView {

  ROOT("fxml/tree/content-root-view.fxml", "tableContainer.title.siardFile", null, null),
  SCHEMAS("fxml/tree/content-root-view.fxml", "tableContainer.title.schemas", null, null),
  SCHEMA("fxml/tree/table-view.fxml", "tableContainer.title.schema", "tableContainer.labelSchema", "tableContainer.labelDescSchema"),
  TABLES("fxml/tree/table-view.fxml", "tableContainer.title.tables", "tableContainer.labelSchema", "tableContainer.labelDescSchema"),
  TABLE("fxml/tree/table-view.fxml", "tableContainer.title.table", "tableContainer.labelTable", "tableContainer.labelNumberOfRows"),
  COLUMNS("fxml/tree/table-view.fxml", "tableContainer.title.columns", "tableContainer.labelTable", "tableContainer.labelNumberOfRows"),
  COLUMN("fxml/tree/column-view.fxml", "tableContainer.title.column", null, null),
  ROWS("fxml/tree/table-view.fxml", "tableContainer.title.data", "tableContainer.labelTable", "tableContainer.labelNumberOfRows");

  private final String viewName;
  private final String viewTitle;
  private final String nameLabel;
  private final String infoLabel;

  TreeContentView(String viewName, String viewTitle, String nameLabel, String infoLabel) {
    this.viewName = viewName;
    this.viewTitle = viewTitle;
    this.nameLabel = nameLabel;
    this.infoLabel = infoLabel;
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

}
