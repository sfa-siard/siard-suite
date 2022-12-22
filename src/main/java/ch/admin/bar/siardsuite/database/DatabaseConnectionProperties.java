package ch.admin.bar.siardsuite.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class DatabaseConnectionProperties {

  private final StringProperty databaseName = new SimpleStringProperty();
  private final StringProperty databaseProduct = new SimpleStringProperty();
  private final StringProperty databasePort = new SimpleStringProperty();
  private final StringProperty databaseUsername = new SimpleStringProperty();
  private String password;
  private final StringProperty connectionUrl = new SimpleStringProperty();
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
    return dbTypes.get(databaseProduct.getValue());
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

  public static final class DatabaseProperties {
    private final String product;
    private final String port;
    private final String defaultUrl;

    public DatabaseProperties(String product, String port, String defaultUrl) {
      this.product = product;
      this.port = port;
      this.defaultUrl = defaultUrl;
    }

    public String product() {
      return product;
    }

    public String port() {
      return port;
    }

    public String defaultUrl() {
      return defaultUrl;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      DatabaseProperties that = (DatabaseProperties) obj;
      return Objects.equals(this.product, that.product) &&
              Objects.equals(this.port, that.port) &&
              Objects.equals(this.defaultUrl, that.defaultUrl);
    }

    @Override
    public int hashCode() {
      return Objects.hash(product, port, defaultUrl);
    }

    @Override
    public String toString() {
      return "DatabaseProperties[" +
              "product=" + product + ", " +
              "port=" + port + ", " +
              "defaultUrl=" + defaultUrl + ']';
    }

    }
}
