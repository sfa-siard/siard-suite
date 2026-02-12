package ch.admin.bar.siard2.jdbc;

import java.sql.*;

public class PostgresNClob
  extends PostgresClob
  implements NClob
{
  public PostgresNClob(PostgresConnection conn, long lOid)
    throws SQLException
  {
    super(conn,lOid);
  }

}
