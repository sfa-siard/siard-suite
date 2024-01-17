package ch.admin.bar.siardsuite.database.model;

import java.util.function.Function;

public interface Dbms<T extends DbmsConnectionProperties> {
    String getName();
    DbmsId getId();
    String getDriverClassName();
    Function<T, String> getJdbcConnectionStringEncoder();
}
