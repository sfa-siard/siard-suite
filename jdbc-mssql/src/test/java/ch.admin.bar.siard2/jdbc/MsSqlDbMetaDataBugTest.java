package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.jdbcx.MsSqlDataSource;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;
import ch.enterag.utils.jdbc.BaseDatabaseMetaDataTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.fail;


// should probably test some obscure bug - but there is no documentation about that and how the db should be set up...
@Ignore
public class MsSqlDbMetaDataBugTest extends BaseDatabaseMetaDataTester {
    private static final ConnectionProperties _cp = new ConnectionProperties();
    private static final String _sDB_URL = MsSqlDriver.getUrl(_cp.getHost() + ":" + _cp.getPort() + ";databaseName=bugdb");
    private static final String _sDB_USER = "buglogin";
    private static final String _sDB_PASSWORD = "bugloginpwd";
    private MsSqlDatabaseMetaData _dmdMsSql = null;

    @Before
    public void setUp() {
        try {
            MsSqlDataSource dsMsSql = new MsSqlDataSource();
            dsMsSql.setUrl(_sDB_URL);
            dsMsSql.setUser(_sDB_USER);
            dsMsSql.setPassword(_sDB_PASSWORD);
            MsSqlConnection connMsSql = (MsSqlConnection) dsMsSql.getConnection();
            _dmdMsSql = (MsSqlDatabaseMetaData) connMsSql.getMetaData();
            setDatabaseMetaData(_dmdMsSql);
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    @Override
    public void testGetTables() {
        enter();
        try {
            BaseDatabaseMetaData bdmd = (BaseDatabaseMetaData) _dmdMsSql;
            System.out.println("Search String Escape: \"" + _dmdMsSql.getSearchStringEscape() + "\"");
            ResultSet rs = _dmdMsSql.getTables(null, bdmd.toPattern("dbo"), bdmd.toPattern("gwz_Cod"), new String[]{"TABLE"});
            print(rs);
            rs.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

}
