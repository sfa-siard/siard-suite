package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseArchive {

 private StringProperty archiveName = new SimpleStringProperty();
  private StringProperty databaseName = new SimpleStringProperty();
  private StringProperty databaseProduct = new SimpleStringProperty();
  private StringProperty connectionUrl = new SimpleStringProperty();
  private static final Map<String, List<String>> dbTypes = Map.of(
          "MS Access", List.of("access", ""),
          "DB/2",  List.of("db2", "50000"),
          "H2 Database", List.of( "h2", "8082"),
          "MySQL",  List.of("mysql", "3306"),
          "Oracle",  List.of("oracle", "1521"),
          "PostgreSQL",  List.of("postgresql", "5432"),
          "Microsoft SQL Server",  List.of("sqlserver", "1433"));
  private StringProperty databaseUsername = new SimpleStringProperty();
  private String password;
  private List<DatabaseSchema> schemas = new ArrayList<>();

  public DatabaseArchive() {}

  public DatabaseArchive(String archiveName, Archive archive) {
    this.archiveName = new SimpleStringProperty(archiveName);
    databaseName = new SimpleStringProperty(archive.getMetaData().getDbName());
    databaseProduct = new SimpleStringProperty(archive.getMetaData().getDatabaseProduct());
    connectionUrl = new SimpleStringProperty(archive.getMetaData().getConnection());
    for (int i = 0; i < archive.getSchemas(); i++) {
      schemas.add(new DatabaseSchema(archive.getSchema(i)));
    }
  }

  public StringProperty getArchiveName() {
    return archiveName;
  }

  public List<String> getDatabaseProductInfo() {
    if (databaseProduct != null) {
      return dbTypes.get(databaseProduct.getValue());
    }
    return null;
  }

  public StringProperty getDatabaseProduct() {
    return databaseProduct;
  }

  public void setDatabaseProduct(String databaseProduct) {
    this.databaseProduct.set(databaseProduct);
  }

  public void setConnectionUrl(String connectionUrl) {
    this.connectionUrl.set(connectionUrl);
  }

  public void setDatabaseName(String name) { this.databaseName.set(name);}
  public StringProperty getDatabaseName() {
    return this.databaseName;
  }

  public StringProperty getConnectionUrl() {
    return this.connectionUrl;
  }

  public StringProperty getDatabaseUsername() {
    return this.databaseUsername;
  }

  public void setDatabaseUsername(String databaseUsername) {
    this.databaseUsername.set(databaseUsername);
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<DatabaseSchema> getSchemas() {
    return schemas;
  }

}
