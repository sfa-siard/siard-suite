package ch.admin.bar.siardsuite.database;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

  public static final Map<String, DatabaseProperties> dbTypes;

  static {
    Map<String, DatabaseProperties> stringDatabasePropertiesMap = new java.util.HashMap<>();
    stringDatabasePropertiesMap.put("MS Access", new DatabaseProperties("access",
                                                                        "",
                                                                        "jdbc:" + PRODUCT + ":D:\\Projekte\\SIARD2\\JdbcAccess\\testfiles\\dbfile.mdb"));
    stringDatabasePropertiesMap.put("DB/2",
                                    new DatabaseProperties("db2",
                                                           "50000",
                                                           "jdbc:" + PRODUCT + ":" + HOST + ":" + PORT + "/" + DB_NAME));
    stringDatabasePropertiesMap.put("MySQL",
                                    new DatabaseProperties("mysql",
                                                           "3306",
                                                           "jdbc:" + PRODUCT + "://" + HOST + ":" + PORT + "/" + DB_NAME));
    stringDatabasePropertiesMap.put("Oracle",
                                    new DatabaseProperties("oracle",
                                                           "1521",
                                                           "jdbc:" + PRODUCT + ":thin:@" + HOST + ":" + PORT + ":" + DB_NAME));
    stringDatabasePropertiesMap.put("PostgreSQL",
                                    new DatabaseProperties("postgresql",
                                                           "5432",
                                                           "jdbc:" + PRODUCT + "://" + HOST + ":" + PORT + "/" + DB_NAME));
    stringDatabasePropertiesMap.put("Microsoft SQL Server", new DatabaseProperties("sqlserver",
                                                                                   "1433",
                                                                                   "jdbc:" + PRODUCT + "://" + HOST + ":" + PORT + ";databaseName=" + DB_NAME));
    dbTypes = new TreeMap<>(
            stringDatabasePropertiesMap);
  }


  public DatabaseConnectionProperties() {}

  public DatabaseProperties getDatabaseProps() {
    return dbTypes.get(databaseProduct.getValue());
  }

  public List<String> getDatabaseTypes() {
    return dbTypes.keySet().stream().collect(Collectors.toList());
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
