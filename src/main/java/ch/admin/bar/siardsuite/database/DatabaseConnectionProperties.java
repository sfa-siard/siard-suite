package ch.admin.bar.siardsuite.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DatabaseConnectionProperties {

  private StringProperty databaseName = new SimpleStringProperty();
  private StringProperty databaseProduct = new SimpleStringProperty();
  private StringProperty databasePort = new SimpleStringProperty();
  private StringProperty databaseUsername = new SimpleStringProperty();
  private String password;
  private StringProperty connectionUrl = new SimpleStringProperty();
  public static final String PRODUCT = "{product}";
  public static final String HOST = "{host}";
  public static final String PORT = "{port}";
  public static final String DB_NAME = "{dbName}";
  public static final Map<String, DatabaseProperties> dbTypes = new TreeMap<>(Map.of(
          "MS Access",
          new DatabaseProperties("access",
                                 "",
                                 "jdbc:" + PRODUCT + ":D:\\Projekte\\SIARD2\\JdbcAccess\\testfiles\\dbfile.mdb"),
          "DB/2",
          new DatabaseProperties("db2", "50000", "jdbc:" + PRODUCT + ":" + HOST + ":" + PORT + "/" + DB_NAME),
          "MySQL",
          new DatabaseProperties("mysql", "3306", "jdbc:" + PRODUCT + "://" + HOST + ":" + PORT + "/" + DB_NAME),
          "Oracle",
          new DatabaseProperties("oracle", "1521", "jdbc:" + PRODUCT + ":thin:@" + HOST + ":" + PORT + ":" + DB_NAME),
          "PostgreSQL",
          new DatabaseProperties("postgresql", "5432", "jdbc:" + PRODUCT + "://" + HOST + ":" + PORT + "/" + DB_NAME),
          "Microsoft SQL Server",
          new DatabaseProperties("sqlserver",
                                 "1433",
                                 "jdbc:" + PRODUCT + "://" + HOST + ":" + PORT + ";databaseName=" + DB_NAME)));


  public DatabaseConnectionProperties() {}

  public DatabaseProperties getDatabaseProps() {
    if (databaseProduct != null) {
      return dbTypes.get(databaseProduct.getValue());
    }
    return null;
  }

  public List<String> getDatabaseTypes() {
    return dbTypes.keySet().stream().toList();
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
