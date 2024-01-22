package ch.admin.bar.siardsuite.database.model;

import ch.admin.bar.siardsuite.util.ThrowingFunction;

import java.util.function.Function;

public interface Dbms<T extends DbmsConnectionProperties> {
    String getName();
    DbmsId getId();
    String getDriverClassName();
    Function<T, String> getJdbcConnectionStringEncoder();
    ThrowingFunction<String, T> getJdbcConnectionStringDecoder();
}
