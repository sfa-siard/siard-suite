package ch.admin.bar.siardsuite.database.model;

import ch.admin.bar.siardsuite.util.FileHelper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Objects;
import java.util.function.Supplier;

@EqualsAndHashCode
@ToString
public class DbmsConnectionData {

    @Getter
    private final Dbms<?> dbms;
    @Getter
    private final DbmsConnectionProperties<?> properties;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Supplier<String> jdbsConnectionStringSupplier;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Supplier<String> userSupplier;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Supplier<String> passwordSupplier;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Supplier<String> dbNameSupplier;

    public DbmsConnectionData(
            @NonNull ServerBasedDbms dbms,
            @NonNull ServerBasedDbmsConnectionProperties properties
    ) {
        this.dbms = dbms;
        this.properties = properties;

        jdbsConnectionStringSupplier = () -> dbms.getJdbcConnectionStringEncoder().apply(properties);
        userSupplier = properties::getUser;
        passwordSupplier = properties::getPassword;
        dbNameSupplier = properties::getDbName;
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
        dbNameSupplier = () -> FileHelper.extractFilenameWithoutExtension(properties.getFile());
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

    public String getDbName() {
        return dbNameSupplier.get();
    }
}
