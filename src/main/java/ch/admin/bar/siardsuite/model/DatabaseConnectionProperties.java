package ch.admin.bar.siardsuite.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

public class DatabaseConnectionProperties {

  private StringProperty databaseName = new SimpleStringProperty();
  private StringProperty databaseProduct = new SimpleStringProperty();
  private StringProperty databasePort = new SimpleStringProperty();
  private StringProperty databaseUsername = new SimpleStringProperty();
  private String password;
  private StringProperty connectionUrl = new SimpleStringProperty();
  private static final Map<String, DatabaseProperties> dbTypes = Map.of(
          "MS Access", new DatabaseProperties("access", "", "jdbc:{product}:D:\\Projekte\\SIARD2\\JdbcAccess\\testfiles\\dbfile.mdb"),
          "DB/2",   new DatabaseProperties("db2", "50000", "jdbc:{product}:{host}:{port}/{dbName}"),
          "H2 Database",  new DatabaseProperties( "h2", "8082", "jdbc:{product}:tcp://{host}:{port}/{dbName}"),
          "MySQL",   new DatabaseProperties("mysql", "3306", "jdbc:{product}://{host}:{port}/{dbName}"),
          "Oracle",   new DatabaseProperties("oracle", "1521", "jdbc:{product}:thin:@{host}:{port}:{dbName}"),
          "PostgreSQL",   new DatabaseProperties("postgresql", "5432", "jdbc:{product}://{host}:{port}/{dbName}"),
          "Microsoft SQL Server",   new DatabaseProperties("sqlserver", "1433", "jdbc:{product}://{host}:{port};databaseName={dbName}"));


  public DatabaseConnectionProperties() {}

  public DatabaseProperties getDatabaseProps() {
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

  public String getPassword() {
    return this.password;
  }

  public record DatabaseProperties(String product, String port, String defaultUrl) {
  }
}
