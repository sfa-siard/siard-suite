package ch.admin.bar.siardsuite.model;

import java.util.Map;

public class SiardArchive {

  private String databaseProduct;
  private static final Map<String, String> dbTypes = Map.of(
          "MS Access", "access",
          "DB/2", "db2",
         "H2 Database", "h2",
          "MySQL", "mysql",
          "Oracle", "oracle",
          "PostgreSQL", "postgresql",
          "Microsoft SQL Server", "sqlserver");

  public SiardArchive() {}

  public String getDatabaseProduct() {
    if (databaseProduct != null) {
      return dbTypes.get(databaseProduct);
    }
    return "undefined";
  }

  public void setDatabaseProduct(String databaseProduct) {
    this.databaseProduct = databaseProduct;
  }

}
