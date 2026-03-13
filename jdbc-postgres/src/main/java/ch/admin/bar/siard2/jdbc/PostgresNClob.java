package ch.admin.bar.siard2.jdbc;

import java.sql.NClob;
import java.sql.SQLException;

public class PostgresNClob
        extends PostgresClob
        implements NClob {
    public PostgresNClob(PostgresConnection conn, long lOid)
            throws SQLException {
        super(conn, lOid);
    }

}
