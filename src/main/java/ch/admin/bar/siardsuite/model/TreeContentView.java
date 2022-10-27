package ch.admin.bar.siardsuite.model;

public enum TreeContentView {

  ROOT("fxml/tree/content-root-view.fxml", "tableContainer.title.siardFile"),
  SCHEMAS("fxml/tree/content-root-view.fxml", "tableContainer.title.schemas"),
  SCHEMA_TABLE("fxml/tree/table-view.fxml", "tableContainer.title.schema"),
  TABLES("fxml/tree/table-view.fxml", "tableContainer.title.tables"),
  TABLE("fxml/tree/table-view.fxml", "tableContainer.title.table"),
  COLUMNS("fxml/tree/table-view.fxml", "tableContainer.title.columns"),
  COLUMN("fxml/tree/column-view.fxml", "tableContainer.title.column"),
  DATA("fxml/tree/table-view.fxml", "tableContainer.title.data");

  private final String viewName;
  private final String viewTitle;

  TreeContentView(String v, String t) {
    this.viewName = v;
    this.viewTitle = t;
  }

  public String getName() {
    return viewName;
  }

  public String getTitle() {
    return viewTitle;
  }


}
