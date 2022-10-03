package ch.admin.bar.siardsuite.model;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SiardArchive {

  private String databaseProduct;
  private String connectionUrl;
  private static final Map<String, List> dbTypes = Map.of(
          "MS Access", List.of("access", ""),
          "DB/2",  List.of("db2", "50000"),
          "H2 Database", List.of( "h2", "8082"),
          "MySQL",  List.of("mysql", "3306"),
          "Oracle",  List.of("oracle", "1521"),
          "PostgreSQL",  List.of("postgresql", "5432"),
          "Microsoft SQL Server",  List.of("sqlserver", "1433"));

  public SiardArchive() {}

  public List getDatabaseProduct() {
    if (databaseProduct != null) {
      return dbTypes.get(databaseProduct);
    }
    return null;
  }

  public void setDatabaseProduct(String databaseProduct) {
    this.databaseProduct = databaseProduct;
  }

  public void setConnectionUrl(String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

}
