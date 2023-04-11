package ch.admin.bar.siardsuite.database;

import java.util.Objects;

public final class DatabaseProperties {
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
