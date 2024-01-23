package ch.admin.bar.siardsuite.database.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.function.Supplier;

public class DbmsConnectionData {
    @Getter
    private final Dbms<?> dbms;
    @Getter
    private final DbmsConnectionProperties<?> properties;

    private final Supplier<String> jdbsConnectionStringSupplier;
    private final Supplier<String> userSupplier;
    private final Supplier<String> passwordSupplier;

    public DbmsConnectionData(
            @NonNull ServerBasedDbms dbms,
            @NonNull ServerBasedDbmsConnectionProperties properties
    ) {
        this.dbms = dbms;
        this.properties = properties;

        jdbsConnectionStringSupplier = () -> dbms.getJdbcConnectionStringEncoder().apply(properties);
        userSupplier = properties::getUser;
        passwordSupplier = properties::getPassword;
    }

    public DbmsConnectionData(
            @NonNull FileBasedDbms dbms,
            @NonNull FileBasedDbmsConnectionProperties properties
    ) {
        this.dbms = dbms;
        this.properties = properties;

        jdbsConnectionStringSupplier = () -> dbms.getJdbcConnectionStringEncoder().apply(properties);
        userSupplier = () -> null;
        passwordSupplier = () -> null;
    }

    public String getUser() {
        return userSupplier.get();
    }

    public String getPassword() {
        return passwordSupplier.get();
    }

    public String getJdbcConnectionString() {
        return jdbsConnectionStringSupplier.get();
    }
}
