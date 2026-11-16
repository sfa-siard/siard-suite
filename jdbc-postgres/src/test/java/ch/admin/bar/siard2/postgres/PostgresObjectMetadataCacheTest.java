package ch.admin.bar.siard2.postgres;

import ch.admin.bar.siard2.jdbc.PostgresConnection;
import ch.admin.bar.siard2.jdbc.PostgresDatabaseMetaData;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.Struct;
import java.sql.Types;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

class PostgresObjectMetadataCacheTest {
    private PostgresDatabaseMetaData metadata(PostgresConnection connection) throws Exception {
        PostgresDatabaseMetaData metadata = mock(PostgresDatabaseMetaData.class);
        when(connection.getMetaData()).thenReturn(metadata);
        when(metadata.toPattern(any())).thenAnswer(invocation -> invocation.getArgument(0));
        return metadata;
    }

    @Test
    void structAttributesAreQueriedOncePerConnectionAndType() throws Exception {
        PostgresConnection connection = mock(PostgresConnection.class);
        PostgresDatabaseMetaData metadata = metadata(connection);
        ResultSet attributes = mock(ResultSet.class);
        when(attributes.next()).thenReturn(true, false);
        when(attributes.getInt("DATA_TYPE")).thenReturn(Types.INTEGER);
        when(attributes.getString("ATTR_TYPE_NAME")).thenReturn("int4");
        when(metadata.getAttributes(nullable(String.class), eq("public"), eq("cached_struct"), eq("%")))
                .thenReturn(attributes);
        Struct struct = mock(Struct.class);
        when(struct.getSQLTypeName()).thenReturn("public.cached_struct");
        when(struct.getAttributes()).thenReturn(new Object[]{7});

        new PostgresObject(struct, Types.STRUCT, "public.cached_struct", connection, "");
        new PostgresObject(struct, Types.STRUCT, "public.cached_struct", connection, "");

        verify(metadata, times(1)).getAttributes(nullable(String.class), eq("public"), eq("cached_struct"), eq("%"));
    }

    @Test
    void distinctBaseTypeIsQueriedOncePerConnectionAndType() throws Exception {
        PostgresConnection connection = mock(PostgresConnection.class);
        PostgresDatabaseMetaData metadata = metadata(connection);
        ResultSet udts = mock(ResultSet.class);
        when(udts.next()).thenReturn(true, false);
        when(udts.getInt("BASE_TYPE")).thenReturn(Types.VARCHAR);
        when(metadata.getUDTs(nullable(String.class), eq("public"), eq("cached_domain"), any(int[].class)))
                .thenReturn(udts);

        new PostgresObject("one", Types.DISTINCT, "public.cached_domain", connection, "");
        new PostgresObject("two", Types.DISTINCT, "public.cached_domain", connection, "");

        verify(metadata, times(1)).getUDTs(nullable(String.class), eq("public"), eq("cached_domain"), any(int[].class));
    }
}
