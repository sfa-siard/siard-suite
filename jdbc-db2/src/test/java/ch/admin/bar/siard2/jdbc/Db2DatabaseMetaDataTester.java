package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.db2.TestDb2Database;
import ch.admin.bar.siard2.db2.TestSqlDatabase;
import ch.admin.bar.siard2.db2.datatype.Db2PredefinedType;
import ch.admin.bar.siard2.jdbcx.Db2DataSource;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.database.SqlTypes;
import ch.enterag.utils.jdbc.BaseDatabaseMetaDataTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.Db2Container;

import java.sql.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Db2DatabaseMetaDataTester extends BaseDatabaseMetaDataTester {

    @ClassRule
    public static Db2Container db2 = new Db2Container("ibmcom/db2:11.5.7.0").acceptLicense();

    private static final String TESTUSER = "TESTUSER";
    private static Pattern _patTYPE = Pattern.compile("^(.*?)(\\(\\s*((\\d+)(\\s*,\\s*(\\d+))?)\\s*\\))?$");

    private Db2DatabaseMetaData _dmdDb2 = null;

    @BeforeClass
    public static void setUpClass() {
        try {
            Db2DataSource dsDb2 = new Db2DataSource();
            dsDb2.setUrl(db2.getJdbcUrl());
            dsDb2.setUser(db2.getUsername());
            dsDb2.setPassword(db2.getPassword());
            Db2Connection connDb2 = (Db2Connection) dsDb2.getConnection();
            /* drop and create the test database granting access to _sDB_USER */
            new TestSqlDatabase(connDb2, TESTUSER);
            new TestDb2Database(connDb2, TESTUSER);
            connDb2.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* setUpClass */

    @Before
    public void setUp() throws SQLException {
        Db2DataSource dsDb2 = new Db2DataSource();
        dsDb2.setUrl(db2.getJdbcUrl());
        dsDb2.setUser(db2.getUsername());
        dsDb2.setPassword(db2.getPassword());
        Connection conn = dsDb2.getConnection();
        conn.setAutoCommit(false);
        _dmdDb2 = (Db2DatabaseMetaData) conn.getMetaData();
        setDatabaseMetaData(_dmdDb2);
    } /* setUp */

    @Test
    public void testClass() {
        assertEquals("Wrong database meta data class!", Db2DatabaseMetaData.class, _dmdDb2.getClass());
    } /* testClass */

    @Test
    public void testGetPseudoColumns() {
        enter();
        try {
            print(_dmdDb2.getPseudoColumns(null, null, "%", "%"));
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
        } // normal users are not authorized ...
    }

    @Override
    @Test
    public void testGetTypeInfo() {
        enter();
        try {
            print(_dmdDb2.getTypeInfo());
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetColumnsDb2Simple() {
        enter();
        try {
            Map<String, TestColumnDefinition> mapCd = new HashMap<String, TestColumnDefinition>();
            for (int iColumn = 0; iColumn < TestDb2Database._listCdSimple.size(); iColumn++) {
                TestColumnDefinition tcd = TestDb2Database._listCdSimple.get(iColumn);
                mapCd.put(tcd.getName(), tcd);
            }
            QualifiedId qiSimple = TestDb2Database.getQualifiedSimpleTable();
            ResultSet rs = _dmdDb2.getColumns(qiSimple.getCatalog(), qiSimple.getSchema(), qiSimple.getName(), "%");
            while (rs.next()) {
                String sColumnName = rs.getString("COLUMN_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                String sTypeName = rs.getString("TYPE_NAME");
                int iColumnSize = rs.getInt("COLUMN_SIZE");
                switch (sTypeName) {
                    case "CHAR":
                        assertEquals("Invalid CHAR mapping!", iDataType, Types.CHAR);
                        break;
                    case "VARCHAR":
                        assertEquals("Invalid VARCHAR mapping!", iDataType, Types.VARCHAR);
                        break;
                    case "CLOB":
                        assertEquals("Invalid CLOB mapping!", iDataType, Types.CLOB);
                        break;
                    case "GRAPHIC":
                        assertEquals("Invalid GRAPHIC mapping!", iDataType, Types.NCHAR);
                        break;
                    case "VARGRAPHIC":
                        assertEquals("Invalid VARGRAPHIC mapping!", iDataType, Types.NVARCHAR);
                        break;
                    case "DBCLOB":
                        assertEquals("Invalid DBCLOB mapping!", iDataType, Types.NCLOB);
                        break;
                    case "CHAR () FOR BIT DATA":
                        assertEquals("Invalid CHAR() FOR BIT DATA mapping!", iDataType, Types.BINARY);
                        break;
                    case "BINARY":
                        assertEquals("Invalid BINARY mapping!", iDataType, Types.BINARY);
                        break;
                    case "VARBINARY":
                        assertEquals("Invalid VARBINARY mapping!", iDataType, Types.VARBINARY);
                        break;
                    case "BLOB":
                        assertEquals("Invalid BLOB mapping!", iDataType, Types.BLOB);
                        break;
                    case "SMALLINT":
                        assertEquals("Invalid SMALLINT mapping!", iDataType, Types.SMALLINT);
                        break;
                    case "INTEGER":
                        assertEquals("Invalid INTEGER mapping!", iDataType, Types.INTEGER);
                        break;
                    case "BIGINT":
                        assertEquals("Invalid BIGINT mapping!", iDataType, Types.BIGINT);
                        break;
                    case "DECIMAL":
                        assertEquals("Invalid DECIMAL mapping!", iDataType, Types.DECIMAL);
                        break;
                    case "DECFLOAT":
                        assertEquals("Invalid DECFLOAT mapping!", iDataType, Types.DECIMAL);
                        break;
                    case "REAL":
                        assertEquals("Invalid REAL mapping!", iDataType, Types.REAL);
                        break;
                    case "DOUBLE":
                        assertEquals("Invalid DOUBLE mapping!", iDataType, Types.DOUBLE);
                        break;
                    case "DATE":
                        assertEquals("Invalid DATE mapping!", iDataType, Types.DATE);
                        break;
                    case "TIME":
                        assertEquals("Invalid TIME mapping!", iDataType, Types.TIME);
                        break;
                    case "TIMESTAMP":
                        assertEquals("Invalid TIMESTAMP mapping!", iDataType, Types.TIMESTAMP);
                        break;
                    case "XML":
                        assertEquals("Invalid XML mapping!", iDataType, Types.SQLXML);
                        break;
                    default:
                        fail("Unexpected type name " + sTypeName + "!");
                }
                TestColumnDefinition tcd = mapCd.get(sColumnName);
                String sType = tcd.getType();
                // parse type
                Matcher matcher = _patTYPE.matcher(sType);
                if (matcher.matches()) {
                    /* compare column size with explicit precision */
                    String sPrecision = matcher.group(4);
                    if (sPrecision != null) {
                        if (iDataType == Types.TIMESTAMP) iColumnSize = iColumnSize - 20;
                        int iPrecision = Integer.parseInt(sPrecision);
                        assertEquals("Explicit precision does not match!", iPrecision, iColumnSize);
                    }
                }
            }
            rs.close();
            /* N.B.: ResultSetMetaData returns wrong column names
             * (NAME instead of TABLE_NAME and COLUMN_NAME)
             * for this result set! */
            print(_dmdDb2.getColumns(qiSimple.getCatalog(), qiSimple.getSchema(), qiSimple.getName(), "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testGetColumnsDb2Simple */

    @Test
    public void testGetColumnsSqlSimple() {
        enter();
        try {
            Map<String, TestColumnDefinition> mapCd = new HashMap<String, TestColumnDefinition>();
            for (int iColumn = 0; iColumn < TestSqlDatabase._listCdSimple.size(); iColumn++) {
                TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(iColumn);
                mapCd.put(tcd.getName(), tcd);
            }
            QualifiedId qiSimple = TestSqlDatabase.getQualifiedSimpleTable();
            ResultSet rs = _dmdDb2.getColumns(qiSimple.getCatalog(), _dmdDb2.toPattern(qiSimple.getSchema()), _dmdDb2.toPattern(qiSimple.getName()), "%");
            while (rs.next()) {
                String sColumnName = rs.getString("COLUMN_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                String sTypeName = rs.getString("TYPE_NAME");
                int iColumnSize = rs.getInt("COLUMN_SIZE");
                switch (sTypeName) {
                    case "CHAR":
                        assertEquals("Invalid CHAR mapping!", iDataType, Types.CHAR);
                        break;
                    case "VARCHAR":
                        assertEquals("Invalid VARCHAR mapping!", iDataType, Types.VARCHAR);
                        break;
                    case "CLOB":
                        assertEquals("Invalid CLOB mapping!", iDataType, Types.CLOB);
                        break;
                    case "GRAPHIC":
                        assertEquals("Invalid GRAPHIC mapping!", iDataType, Types.NCHAR);
                        break;
                    case "VARGRAPHIC":
                        assertEquals("Invalid VARGRAPHIC mapping!", iDataType, Types.NVARCHAR);
                        break;
                    case "DBCLOB":
                        assertEquals("Invalid DBCLOB mapping!", iDataType, Types.NCLOB);
                        break;
                    case "CHAR () FOR BIT DATA":
                        assertEquals("Invalid CHAR() FOR BIT DATA mapping!", iDataType, Types.BINARY);
                        break;
                    case "BINARY":
                        assertEquals("Invalid BINARY mapping!", iDataType, Types.BINARY);
                        break;
                    case "VARBINARY":
                        assertEquals("Invalid VARCHAR FOR BIT DATA mapping!", iDataType, Types.VARBINARY);
                        break;
                    case "BLOB":
                        assertEquals("Invalid BLOB mapping!", iDataType, Types.BLOB);
                        break;
                    case "SMALLINT":
                        assertEquals("Invalid SMALLINT mapping!", iDataType, Types.SMALLINT);
                        break;
                    case "INTEGER":
                        assertEquals("Invalid INTEGER mapping!", iDataType, Types.INTEGER);
                        break;
                    case "BIGINT":
                        assertEquals("Invalid BIGINT mapping!", iDataType, Types.BIGINT);
                        break;
                    case "DECIMAL":
                        assertEquals("Invalid DECIMAL mapping!", iDataType, Types.DECIMAL);
                        break;
                    case "DECFLOAT":
                        assertEquals("Invalid DECFLOAT mapping!", iDataType, Types.DECIMAL);
                        break;
                    case "REAL":
                        assertEquals("Invalid REAL mapping!", iDataType, Types.REAL);
                        break;
                    case "DOUBLE":
                        assertEquals("Invalid DOUBLE mapping!", iDataType, Types.DOUBLE);
                        break;
                    case "DATE":
                        assertEquals("Invalid DATE mapping!", iDataType, Types.DATE);
                        break;
                    case "TIME":
                        assertEquals("Invalid TIME mapping!", iDataType, Types.TIME);
                        break;
                    case "TIMESTAMP":
                        assertEquals("Invalid TIMESTAMP mapping!", iDataType, Types.TIMESTAMP);
                        break;
                    case "XML":
                        assertEquals("Invalid XML mapping!", iDataType, Types.SQLXML);
                        break;
                    case "INTERVAL YEAR TO MONTH":
                        assertEquals("Invalid INTERVAL mapping!", iDataType, Types.OTHER);
                        break;
                    case "INTERVAL DAY TO SECOND":
                        assertEquals("Invalid INTERVAL mapping!", iDataType, Types.OTHER);
                        break;
                    default:
                        assertEquals("Invalid DISTINCT mapping!", iDataType, Types.DISTINCT);
                        break;
                }
                TestColumnDefinition tcd = mapCd.get(sColumnName);
                String sType = tcd.getType();
                // parse type
                Matcher matcher = _patTYPE.matcher(sType);
                if (matcher.matches()) {
                    /* compare column size with explicit precision */
                    String sPrecision = matcher.group(4);
                    if (sPrecision != null) {
                        int iPrecision = Integer.parseInt(sPrecision);
                        if (iDataType == Types.TIMESTAMP) iColumnSize = iColumnSize - 20;
                        else if ((iDataType == Types.REAL) && (iColumnSize > iPrecision)) iColumnSize = iPrecision;
                        else if (iDataType == Types.OTHER) iColumnSize = iPrecision;
                        assertEquals("Explicit precision does not match!", iPrecision, iColumnSize);
                    }
                }
            }
            rs.close();
            /* N.B.: ResultSetMetaData returns wrong column names
             * (NAME instead of TABLE_NAME and COLUMN_NAME)
             * for this result set! */
            print(_dmdDb2.getColumns(qiSimple.getCatalog(), qiSimple.getSchema(), qiSimple.getName(), "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testGetColumnsDb2Simple */

    @Test
    public void testGetColumnsDb2Complex() {
        enter();
        try {
            Map<String, TestColumnDefinition> mapCd = new HashMap<String, TestColumnDefinition>();
            for (int iColumn = 0; iColumn < TestDb2Database._listCdComplex.size(); iColumn++) {
                TestColumnDefinition tcd = TestDb2Database._listCdComplex.get(iColumn);
                mapCd.put(tcd.getName(), tcd);
            }
            QualifiedId qiComplex = TestDb2Database.getQualifiedComplexTable();
            ResultSet rs = _dmdDb2.getColumns(qiComplex.getCatalog(), qiComplex.getSchema(), qiComplex.getName(), "%");
            while (rs.next()) {
                String sColumnName = rs.getString("COLUMN_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                String sTypeName = rs.getString("TYPE_NAME");
                int iColumnSize = rs.getInt("COLUMN_SIZE");
                if ((iDataType != Types.DISTINCT) && (iDataType != Types.STRUCT)) {
                    switch (sTypeName) {
                        case "CHAR":
                            assertEquals("Invalid CHAR mapping!", iDataType, Types.CHAR);
                            break;
                        case "VARCHAR":
                            assertEquals("Invalid VARCHAR mapping!", iDataType, Types.VARCHAR);
                            break;
                        case "CLOB":
                            assertEquals("Invalid CLOB mapping!", iDataType, Types.CLOB);
                            break;
                        case "GRAPHIC":
                            assertEquals("Invalid GRAPHIC mapping!", iDataType, Types.NCHAR);
                            break;
                        case "VARGRAPHIC":
                            assertEquals("Invalid VARGRAPHIC mapping!", iDataType, Types.NVARCHAR);
                            break;
                        case "DBCLOB":
                            assertEquals("Invalid DBCLOB mapping!", iDataType, Types.NCLOB);
                            break;
                        case "CHAR () FOR BIT DATA":
                            assertEquals("Invalid CHAR() FOR BIT DATA mapping!", iDataType, Types.BINARY);
                            break;
                        case "BINARY":
                            assertEquals("Invalid BINARY mapping!", iDataType, Types.BINARY);
                            break;
                        case "VARCHAR () FOR BIT DATA":
                            assertEquals("Invalid VARCHAR FOR BIT DATA mapping!", iDataType, Types.VARBINARY);
                            break;
                        case "BLOB":
                            assertEquals("Invalid BLOB mapping!", iDataType, Types.BLOB);
                            break;
                        case "SMALLINT":
                            assertEquals("Invalid SMALLINT mapping!", iDataType, Types.SMALLINT);
                            break;
                        case "INTEGER":
                            assertEquals("Invalid INTEGER mapping!", iDataType, Types.INTEGER);
                            break;
                        case "BIGINT":
                            assertEquals("Invalid BIGINT mapping!", iDataType, Types.BIGINT);
                            break;
                        case "DECIMAL":
                            assertEquals("Invalid DECIMAL mapping!", iDataType, Types.DECIMAL);
                            break;
                        case "DECFLOAT":
                            assertEquals("Invalid DECFLOAT mapping!", iDataType, Types.FLOAT);
                            break;
                        case "REAL":
                            assertEquals("Invalid REAL mapping!", iDataType, Types.REAL);
                            break;
                        case "DOUBLE":
                            assertEquals("Invalid DOUBLE mapping!", iDataType, Types.DOUBLE);
                            break;
                        case "DATE":
                            assertEquals("Invalid DATE mapping!", iDataType, Types.DATE);
                            break;
                        case "TIME":
                            assertEquals("Invalid TIME mapping!", iDataType, Types.TIME);
                            break;
                        case "TIMESTAMP":
                            assertEquals("Invalid TIMESTAMP mapping!", iDataType, Types.TIMESTAMP);
                            break;
                        case "XML":
                            assertEquals("Invalid XML mapping!", iDataType, Types.SQLXML);
                            break;
                        default:
                            try {
                                QualifiedId qiType = new QualifiedId(sTypeName);
                                if (qiType.getName()
                                          .equals(Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE) || qiType.getName()
                                                                                                        .equals(Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE))
                                    assertEquals("Invalid INTERVAL mapping!", iDataType, Types.OTHER);
                                else assertEquals("Invalid DISTINCT mapping!", iDataType, Types.DISTINCT);
                            } catch (ParseException pe) {
                                throw new SQLException("Type \"" + sTypeName + "\" could not be parsed!", pe);
                            }
                            break;
                    }
                    TestColumnDefinition tcd = mapCd.get(sColumnName);
                    String sType = tcd.getType();
                    // parse type
                    Matcher matcher = _patTYPE.matcher(sType);
                    if (matcher.matches()) {
                        /* compare column size with explicit precision */
                        String sPrecision = matcher.group(4);
                        if (sPrecision != null) {
                            int iPrecision = Integer.parseInt(sPrecision);
                            if (iDataType == Types.TIMESTAMP) iColumnSize = iColumnSize - 20;
                            else if ((iDataType == Types.REAL) && (iColumnSize > iPrecision)) iColumnSize = iPrecision;
                            else if (iDataType == Types.OTHER) iColumnSize = iPrecision;
                            assertEquals("Explicit precision does not match!", iPrecision, iColumnSize);
                        }
                    }
                } else {
                    TestColumnDefinition tcd = mapCd.get(sColumnName);
                    try {
                        QualifiedId qiTypeName = new QualifiedId(sTypeName);
                        assertEquals("Invalid UDT type!", tcd.getType(), qiTypeName.format());
                    } catch (ParseException pe) {
                        fail(EU.getExceptionMessage(pe));
                    }
                }
            }
            rs.close();
            /* N.B.: ResultSetMetaData returns wrong column names
             * (NAME instead of TABLE_NAME and COLUMN_NAME)
             * for this result set! */
            print(_dmdDb2.getColumns(qiComplex.getCatalog(), qiComplex.getSchema(), qiComplex.getName(), "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testGetColumnsDb2Complex */

    @Test
    public void testGetColumnsSqlComplex() {
        enter();
        try {
            Map<String, TestColumnDefinition> mapCd = new HashMap<String, TestColumnDefinition>();
            for (int iColumn = 0; iColumn < TestSqlDatabase._listCdComplex.size(); iColumn++) {
                TestColumnDefinition tcd = TestSqlDatabase._listCdComplex.get(iColumn);
                mapCd.put(tcd.getName(), tcd);
            }
            QualifiedId qiComplex = TestSqlDatabase.getQualifiedComplexTable();
            ResultSet rs = _dmdDb2.getColumns(qiComplex.getCatalog(), qiComplex.getSchema(), qiComplex.getName(), "%");
            while (rs.next()) {
                String sColumnName = rs.getString("COLUMN_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                String sTypeName = rs.getString("TYPE_NAME");
                int iColumnSize = rs.getInt("COLUMN_SIZE");
                if ((iDataType != Types.DISTINCT) && (iDataType != Types.STRUCT)) {
                    switch (sTypeName) {
                        case "CHAR":
                            assertEquals("Invalid CHAR mapping!", iDataType, Types.CHAR);
                            break;
                        case "VARCHAR":
                            assertEquals("Invalid VARCHAR mapping!", iDataType, Types.VARCHAR);
                            break;
                        case "CLOB":
                            assertEquals("Invalid CLOB mapping!", iDataType, Types.CLOB);
                            break;
                        case "GRAPHIC":
                            assertEquals("Invalid GRAPHIC mapping!", iDataType, Types.NCHAR);
                            break;
                        case "VARGRAPHIC":
                            assertEquals("Invalid VARGRAPHIC mapping!", iDataType, Types.NVARCHAR);
                            break;
                        case "DBCLOB":
                            assertEquals("Invalid DBCLOB mapping!", iDataType, Types.NCLOB);
                            break;
                        case "CHAR () FOR BIT DATA":
                            assertEquals("Invalid CHAR() FOR BIT DATA mapping!", iDataType, Types.BINARY);
                            break;
                        case "BINARY":
                            assertEquals("Invalid BINARY mapping!", iDataType, Types.BINARY);
                            break;
                        case "VARCHAR () FOR BIT DATA":
                            assertEquals("Invalid VARCHAR FOR BIT DATA mapping!", iDataType, Types.VARBINARY);
                            break;
                        case "BLOB":
                            assertEquals("Invalid BLOB mapping!", iDataType, Types.BLOB);
                            break;
                        case "SMALLINT":
                            assertEquals("Invalid SMALLINT mapping!", iDataType, Types.SMALLINT);
                            break;
                        case "INTEGER":
                            assertEquals("Invalid INTEGER mapping!", iDataType, Types.INTEGER);
                            break;
                        case "BIGINT":
                            assertEquals("Invalid BIGINT mapping!", iDataType, Types.BIGINT);
                            break;
                        case "DECIMAL":
                            assertEquals("Invalid DECIMAL mapping!", iDataType, Types.DECIMAL);
                            break;
                        case "DECFLOAT":
                            assertEquals("Invalid DECFLOAT mapping!", iDataType, Types.FLOAT);
                            break;
                        case "REAL":
                            assertEquals("Invalid REAL mapping!", iDataType, Types.REAL);
                            break;
                        case "DOUBLE":
                            assertEquals("Invalid DOUBLE mapping!", iDataType, Types.DOUBLE);
                            break;
                        case "DATE":
                            assertEquals("Invalid DATE mapping!", iDataType, Types.DATE);
                            break;
                        case "TIME":
                            assertEquals("Invalid TIME mapping!", iDataType, Types.TIME);
                            break;
                        case "TIMESTAMP":
                            assertEquals("Invalid TIMESTAMP mapping!", iDataType, Types.TIMESTAMP);
                            break;
                        case "XML":
                            assertEquals("Invalid XML mapping!", iDataType, Types.SQLXML);
                            break;
                        default:
                            try {
                                QualifiedId qiType = new QualifiedId(sTypeName);
                                if (qiType.getName()
                                          .equals(Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE) || qiType.getName()
                                                                                                        .equals(Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE))
                                    assertEquals("Invalid INTERVAL mapping!", iDataType, Types.OTHER);
                                else assertEquals("Invalid DISTINCT mapping!", iDataType, Types.DISTINCT);
                            } catch (ParseException pe) {
                                throw new SQLException("Type \"" + sTypeName + "\" could not be parsed!", pe);
                            }
                            break;
                    }
                    TestColumnDefinition tcd = mapCd.get(sColumnName);
                    String sType = tcd.getType();
                    // parse type
                    Matcher matcher = _patTYPE.matcher(sType);
                    if (matcher.matches()) {
                        /* compare column size with explicit precision */
                        String sPrecision = matcher.group(4);
                        if (sPrecision != null) {
                            int iPrecision = Integer.parseInt(sPrecision);
                            if (iDataType == Types.TIMESTAMP) iColumnSize = iColumnSize - 20;
                            else if ((iDataType == Types.REAL) && (iColumnSize > iPrecision)) iColumnSize = iPrecision;
                            else if (iDataType == Types.OTHER) iColumnSize = iPrecision;
                            assertEquals("Explicit precision does not match!", iPrecision, iColumnSize);
                        }
                    }
                } else {
                    TestColumnDefinition tcd = mapCd.get(sColumnName);
                    try {
                        QualifiedId qiTypeName = new QualifiedId(sTypeName);
                        assertEquals("Invalid UDT type!", tcd.getType(), qiTypeName.format());
                    } catch (ParseException pe) {
                        fail(EU.getExceptionMessage(pe));
                    }
                }
            }
            rs.close();
            /* N.B.: ResultSetMetaData returns wrong column names
             * (NAME instead of TABLE_NAME and COLUMN_NAME)
             * for this result set! */
            print(_dmdDb2.getColumns(qiComplex.getCatalog(), qiComplex.getSchema(), qiComplex.getName(), "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    } /* testGetColumnsSqlComplex */

    @Test
    public void testGetUDTsSample() {
        enter();
        try {
            ResultSet rs = _dmdDb2.getUDTs(null, "TESTDB2", "TDISTINCT", new int[]{Types.STRUCT, Types.DISTINCT});
            while (rs.next()) {
                String sCatalog = rs.getString("TYPE_CAT");
                String sSchema = rs.getString("TYPE_SCHEM");
                String sTypeName = rs.getString("TYPE_NAME");
                QualifiedId qiType = new QualifiedId(sCatalog, sSchema, sTypeName);
                String sClassName = rs.getString("CLASS_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                int iBaseType = rs.getInt("BASE_TYPE");
                /* base type is mapped! Its precision and its scale have to be retrieved from getColumns() of a column with this type name! */
                System.out.println(qiType.format() + "\t" + sClassName + "\t" + String.valueOf(iDataType) + " (" + SqlTypes.getTypeName(iDataType) + ")\t" + String.valueOf(iBaseType) + " (" + SqlTypes.getTypeName(iBaseType) + ")");
            }
            rs.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetUDTsDb2Distinct() {
        enter();
        try {
            QualifiedId qiDistinctType = TestDb2Database.getQualifiedDistinctType();
            ResultSet rs = _dmdDb2.getUDTs(qiDistinctType.getCatalog(), qiDistinctType.getSchema(), qiDistinctType.getName(), null);
            while (rs.next()) {
                String sCatalog = rs.getString("TYPE_CAT");
                String sSchema = rs.getString("TYPE_SCHEM");
                String sTypeName = rs.getString("TYPE_NAME");
                QualifiedId qiType = new QualifiedId(sCatalog, sSchema, sTypeName);
                String sClassName = rs.getString("CLASS_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                int iBaseType = rs.getInt("BASE_TYPE");
                /* base type is mapped! Its precision and its scale have to be retrieved from getColumns() of a column with this type name! */
                System.out.println(qiType.format() + "\t" + sClassName + "\t" + String.valueOf(iDataType) + " (" + SqlTypes.getTypeName(iDataType) + ")\t" + String.valueOf(iBaseType) + " (" + SqlTypes.getTypeName(iBaseType) + ")");
            }
            rs.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetUDTsSqlDistinct() {
        enter();
        try {
            QualifiedId qiDistinctType = TestSqlDatabase.getQualifiedDistinctType();
            ResultSet rs = _dmdDb2.getUDTs(qiDistinctType.getCatalog(), qiDistinctType.getSchema(), qiDistinctType.getName(), null);
            while (rs.next()) {
                String sCatalog = rs.getString("TYPE_CAT");
                String sSchema = rs.getString("TYPE_SCHEM");
                String sTypeName = rs.getString("TYPE_NAME");
                QualifiedId qiType = new QualifiedId(sCatalog, sSchema, sTypeName);
                String sClassName = rs.getString("CLASS_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                int iBaseType = rs.getInt("BASE_TYPE");
                /* base type is mapped! Its precision and its scale have to be retrieved from getColumns() of a column with this type name! */
                System.out.println(qiType.format() + "\t" + sClassName + "\t" + String.valueOf(iDataType) + " (" + SqlTypes.getTypeName(iDataType) + ")\t" + String.valueOf(iBaseType) + " (" + SqlTypes.getTypeName(iBaseType) + ")");
            }
            rs.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetUDTsDb2Struct() {
        enter();
        try {
            QualifiedId qiStructType = TestDb2Database.getQualifiedStructType();
            ResultSet rs = _dmdDb2.getUDTs(qiStructType.getCatalog(), qiStructType.getSchema(), qiStructType.getName(), null);
            while (rs.next()) {
                String sCatalog = rs.getString("TYPE_CAT");
                String sSchema = rs.getString("TYPE_SCHEM");
                String sTypeName = rs.getString("TYPE_NAME");
                QualifiedId qiType = new QualifiedId(sCatalog, sSchema, sTypeName);
                String sClassName = rs.getString("CLASS_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                int iBaseType = rs.getInt("BASE_TYPE");
                /* base type is mapped! Its precision and its scale have to be retrieved from getColumns() of a column with this type name! */
                System.out.println(qiType.format() + "\t" + sClassName + "\t" + String.valueOf(iDataType) + " (" + SqlTypes.getTypeName(iDataType) + ")\t" + String.valueOf(iBaseType) + " (" + SqlTypes.getTypeName(iBaseType) + ")");
            }
            rs.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetUDTsSqlStruct() {
        enter();
        try {
            QualifiedId qiStructType = TestSqlDatabase.getQualifiedSimpleType();
            ResultSet rs = _dmdDb2.getUDTs(qiStructType.getCatalog(), qiStructType.getSchema(), qiStructType.getName(), null);
            while (rs.next()) {
                String sCatalog = rs.getString("TYPE_CAT");
                String sSchema = rs.getString("TYPE_SCHEM");
                String sTypeName = rs.getString("TYPE_NAME");
                QualifiedId qiType = new QualifiedId(sCatalog, sSchema, sTypeName);
                String sClassName = rs.getString("CLASS_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                int iBaseType = rs.getInt("BASE_TYPE");
                /* base type is mapped! Its precision and its scale have to be retrieved from getColumns() of a column with this type name! */
                System.out.println(qiType.format() + "\t" + sClassName + "\t" + String.valueOf(iDataType) + " (" + SqlTypes.getTypeName(iDataType) + ")\t" + String.valueOf(iBaseType) + " (" + SqlTypes.getTypeName(iBaseType) + ")");
            }
            rs.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetUDTs() {
        enter();
        try {
            ResultSet rs = _dmdDb2.getUDTs(null, "%", "%", null);
            while (rs.next()) {
                String sCatalog = rs.getString("TYPE_CAT");
                String sSchema = rs.getString("TYPE_SCHEM");
                String sTypeName = rs.getString("TYPE_NAME");
                QualifiedId qiType = new QualifiedId(sCatalog, sSchema, sTypeName);
                String sClassName = rs.getString("CLASS_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                int iBaseType = rs.getInt("BASE_TYPE");
                /* base type is mapped! Its precision and its scale have to be retrieved from getColumns() of a column with this type name! */
                System.out.println(qiType.format() + "\t" + sClassName + "\t" + String.valueOf(iDataType) + " (" + SqlTypes.getTypeName(iDataType) + ")\t" + String.valueOf(iBaseType) + " (" + SqlTypes.getTypeName(iBaseType) + ")");
            }
            rs.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetAttributesDb2() {
        enter();
        try {
            Map<String, TestColumnDefinition> mapAd = new HashMap<String, TestColumnDefinition>();
            for (int iAttribute = 0; iAttribute < TestDb2Database._listAdStruct.size(); iAttribute++) {
                TestColumnDefinition tcd = TestDb2Database._listAdStruct.get(iAttribute);
                mapAd.put(tcd.getName(), tcd);
            }
            QualifiedId qiStructType = TestDb2Database.getQualifiedStructType();
            ResultSet rs = _dmdDb2.getAttributes(qiStructType.getCatalog(), qiStructType.getSchema(), qiStructType.getName(), "%");
            while (rs.next()) {
                String sCatalog = rs.getString("TYPE_CAT");
                assertEquals("Wrong catalog!", TestDb2Database.getQualifiedStructType()
                                                              .getCatalog(), sCatalog);
                String sSchema = rs.getString("TYPE_SCHEM");
                assertEquals("Wrong schema!", TestDb2Database.getQualifiedStructType()
                                                             .getSchema(), sSchema);
                String sTypeName = rs.getString("TYPE_NAME");
                assertEquals("Wrong type!", TestDb2Database.getQualifiedStructType()
                                                           .getName(), sTypeName);
                String sAttrName = rs.getString("ATTR_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                String sAttrTypeName = rs.getString("ATTR_TYPE_NAME");
                int iAttrSize = rs.getInt("ATTR_SIZE");
                if ((iDataType != Types.DISTINCT) && (iDataType != Types.STRUCT)) {
                    switch (sAttrTypeName) {
                        case "CHAR":
                            assertEquals("Invalid CHAR mapping!", iDataType, Types.CHAR);
                            break;
                        case "VARCHAR":
                            assertEquals("Invalid VARCHAR mapping!", iDataType, Types.VARCHAR);
                            break;
                        case "CLOB":
                            assertEquals("Invalid CLOB mapping!", iDataType, Types.CLOB);
                            break;
                        case "GRAPHIC":
                            assertEquals("Invalid GRAPHIC mapping!", iDataType, Types.NCHAR);
                            break;
                        case "VARGRAPHIC":
                            assertEquals("Invalid VARGRAPHIC mapping!", iDataType, Types.NVARCHAR);
                            break;
                        case "DBCLOB":
                            assertEquals("Invalid DBCLOB mapping!", iDataType, Types.NCLOB);
                            break;
                        case "CHAR () FOR BIT DATA":
                            assertEquals("Invalid CHAR() FOR BIT DATA mapping!", iDataType, Types.BINARY);
                            break;
                        case "BINARY":
                            assertEquals("Invalid BINARY mapping!", iDataType, Types.BINARY);
                            break;
                        case "VARCHAR () FOR BIT DATA":
                            assertEquals("Invalid VARCHAR FOR BIT DATA mapping!", iDataType, Types.VARBINARY);
                            break;
                        case "BLOB":
                            assertEquals("Invalid BLOB mapping!", iDataType, Types.BLOB);
                            break;
                        case "SMALLINT":
                            assertEquals("Invalid SMALLINT mapping!", iDataType, Types.SMALLINT);
                            break;
                        case "INTEGER":
                            assertEquals("Invalid INTEGER mapping!", iDataType, Types.INTEGER);
                            break;
                        case "BIGINT":
                            assertEquals("Invalid BIGINT mapping!", iDataType, Types.BIGINT);
                            break;
                        case "DECIMAL":
                            assertEquals("Invalid DECIMAL mapping!", iDataType, Types.DECIMAL);
                            break;
                        case "DECFLOAT":
                            assertEquals("Invalid DECFLOAT mapping!", iDataType, Types.FLOAT);
                            break;
                        case "REAL":
                            assertEquals("Invalid REAL mapping!", iDataType, Types.REAL);
                            break;
                        case "DOUBLE":
                            assertEquals("Invalid DOUBLE mapping!", iDataType, Types.DOUBLE);
                            break;
                        case "DATE":
                            assertEquals("Invalid DATE mapping!", iDataType, Types.DATE);
                            break;
                        case "TIME":
                            assertEquals("Invalid TIME mapping!", iDataType, Types.TIME);
                            break;
                        case "TIMESTAMP":
                            assertEquals("Invalid TIMESTAMP mapping!", iDataType, Types.TIMESTAMP);
                            break;
                        case "XML":
                            assertEquals("Invalid XML mapping!", iDataType, Types.SQLXML);
                            break;
                        default:
                            try {
                                QualifiedId qiType = new QualifiedId(sTypeName);
                                if (qiType.getName()
                                          .equals(Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE) || qiType.getName()
                                                                                                        .equals(Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE))
                                    assertEquals("Invalid INTERVAL mapping!", iDataType, Types.OTHER);
                                else assertEquals("Invalid DISTINCT mapping!", iDataType, Types.DISTINCT);
                            } catch (ParseException pe) {
                                throw new SQLException("Type \"" + sTypeName + "\" could not be parsed!", pe);
                            }
                            break;
                    }
                    TestColumnDefinition tad = mapAd.get(sAttrName);
                    String sType = tad.getType();
                    // parse type
                    Matcher matcher = _patTYPE.matcher(sType);
                    if (matcher.matches()) {
                        /* compare column size with explicit precision */
                        String sPrecision = matcher.group(4);
                        if (sPrecision != null) {
                            int iPrecision = Integer.parseInt(sPrecision);
                            assertEquals("Explicit precision does not match!", iPrecision, iAttrSize);
                        }
                    }
                } else {
                    TestColumnDefinition tad = mapAd.get(sAttrName);
                    try {
                        QualifiedId qiTypeName = new QualifiedId(sAttrTypeName);
                        assertEquals("Invalid UDT type!", tad.getType(), qiTypeName.format());
                    } catch (ParseException pe) {
                        fail(EU.getExceptionMessage(pe));
                    }
                }
            }
            rs.close();
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetAttributesSql() {
        enter();
        try {
            Map<String, TestColumnDefinition> mapAd = new HashMap<String, TestColumnDefinition>();
            for (int iAttribute = 0; iAttribute < TestSqlDatabase._listAdComplex.size(); iAttribute++) {
                TestColumnDefinition tcd = TestSqlDatabase._listAdComplex.get(iAttribute);
                mapAd.put(tcd.getName(), tcd);
            }
            QualifiedId qiStructType = TestSqlDatabase.getQualifiedComplexType();
            ResultSet rs = _dmdDb2.getAttributes(qiStructType.getCatalog(), qiStructType.getSchema(), qiStructType.getName(), "%");
            while (rs.next()) {
                String sCatalog = rs.getString("TYPE_CAT");
                assertEquals("Wrong catalog!", TestSqlDatabase.getQualifiedComplexType()
                                                              .getCatalog(), sCatalog);
                String sSchema = rs.getString("TYPE_SCHEM");
                assertEquals("Wrong schema!", TestSqlDatabase.getQualifiedComplexType()
                                                             .getSchema(), sSchema);
                String sTypeName = rs.getString("TYPE_NAME");
                assertEquals("Wrong type!", TestSqlDatabase.getQualifiedComplexType()
                                                           .getName(), sTypeName);
                String sAttrName = rs.getString("ATTR_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                String sAttrTypeName = rs.getString("ATTR_TYPE_NAME");
                int iAttrSize = rs.getInt("ATTR_SIZE");
                if ((iDataType != Types.DISTINCT) && (iDataType != Types.STRUCT)) {
                    switch (sAttrTypeName) {
                        case "CHAR":
                            assertEquals("Invalid CHAR mapping!", iDataType, Types.CHAR);
                            break;
                        case "VARCHAR":
                            assertEquals("Invalid VARCHAR mapping!", iDataType, Types.VARCHAR);
                            break;
                        case "CLOB":
                            assertEquals("Invalid CLOB mapping!", iDataType, Types.CLOB);
                            break;
                        case "GRAPHIC":
                            assertEquals("Invalid GRAPHIC mapping!", iDataType, Types.NCHAR);
                            break;
                        case "VARGRAPHIC":
                            assertEquals("Invalid VARGRAPHIC mapping!", iDataType, Types.NVARCHAR);
                            break;
                        case "DBCLOB":
                            assertEquals("Invalid DBCLOB mapping!", iDataType, Types.NCLOB);
                            break;
                        case "CHAR () FOR BIT DATA":
                            assertEquals("Invalid CHAR() FOR BIT DATA mapping!", iDataType, Types.BINARY);
                            break;
                        case "BINARY":
                            assertEquals("Invalid BINARY mapping!", iDataType, Types.BINARY);
                            break;
                        case "VARCHAR () FOR BIT DATA":
                            assertEquals("Invalid VARCHAR FOR BIT DATA mapping!", iDataType, Types.VARBINARY);
                            break;
                        case "BLOB":
                            assertEquals("Invalid BLOB mapping!", iDataType, Types.BLOB);
                            break;
                        case "SMALLINT":
                            assertEquals("Invalid SMALLINT mapping!", iDataType, Types.SMALLINT);
                            break;
                        case "INTEGER":
                            assertEquals("Invalid INTEGER mapping!", iDataType, Types.INTEGER);
                            break;
                        case "BIGINT":
                            assertEquals("Invalid BIGINT mapping!", iDataType, Types.BIGINT);
                            break;
                        case "DECIMAL":
                            assertEquals("Invalid DECIMAL mapping!", iDataType, Types.DECIMAL);
                            break;
                        case "DECFLOAT":
                            assertEquals("Invalid DECFLOAT mapping!", iDataType, Types.FLOAT);
                            break;
                        case "REAL":
                            assertEquals("Invalid REAL mapping!", iDataType, Types.REAL);
                            break;
                        case "DOUBLE":
                            assertEquals("Invalid DOUBLE mapping!", iDataType, Types.DOUBLE);
                            break;
                        case "DATE":
                            assertEquals("Invalid DATE mapping!", iDataType, Types.DATE);
                            break;
                        case "TIME":
                            assertEquals("Invalid TIME mapping!", iDataType, Types.TIME);
                            break;
                        case "TIMESTAMP":
                            assertEquals("Invalid TIMESTAMP mapping!", iDataType, Types.TIMESTAMP);
                            break;
                        case "XML":
                            assertEquals("Invalid XML mapping!", iDataType, Types.SQLXML);
                            break;
                        default:
                            try {
                                QualifiedId qiType = new QualifiedId(sTypeName);
                                if (qiType.getName()
                                          .equals(Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE) || qiType.getName()
                                                                                                        .equals(Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE))
                                    assertEquals("Invalid INTERVAL mapping!", iDataType, Types.OTHER);
                                else assertEquals("Invalid DISTINCT mapping!", iDataType, Types.DISTINCT);
                            } catch (ParseException pe) {
                                throw new SQLException("Type \"" + sTypeName + "\" could not be parsed!", pe);
                            }
                            break;
                    }
                    TestColumnDefinition tad = mapAd.get(sAttrName);
                    String sType = tad.getType();
                    // parse type
                    Matcher matcher = _patTYPE.matcher(sType);
                    if (matcher.matches()) {
                        /* compare column size with explicit precision */
                        String sPrecision = matcher.group(4);
                        if (sPrecision != null) {
                            int iPrecision = Integer.parseInt(sPrecision);
                            assertEquals("Explicit precision does not match!", iPrecision, iAttrSize);
                        }
                    }
                } else {
                    TestColumnDefinition tad = mapAd.get(sAttrName);
                    try {
                        QualifiedId qiTypeName = new QualifiedId(sAttrTypeName);
                        assertEquals("Invalid UDT type!", tad.getType(), qiTypeName.format());
                    } catch (ParseException pe) {
                        fail(EU.getExceptionMessage(pe));
                    }
                }
            }
            rs.close();
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Override
    @Test
    public void testGetTableTypes() {
        enter();
        try {
            print(_dmdDb2.getTableTypes());
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Override
    @Test
    public void testGetTables() {
        enter();
        try {
            print(_dmdDb2.getTables(null, null, "%", new String[]{"TABLE"}));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetViews() {
        enter();
        try {
            print(_dmdDb2.getTables(null, "%", "%", new String[]{"VIEW"}));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetFunctions() {
        enter();
        try {
            print(_dmdDb2.getFunctions(null, TestDb2Database._sTEST_SCHEMA, "%"));
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test

    public void testGetFunctionColumns() {
        enter();
        try {
            print(_dmdDb2.getFunctionColumns(null, TestDb2Database._sTEST_SCHEMA, "TDB2DISTINCT", "%"));
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetProcedures() {
        enter();
        try {
            print(_dmdDb2.getProcedures(null, TestDb2Database._sTEST_SCHEMA, "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetMaxBinaryLiteralLength() {
        enter();
        try {
            println(String.valueOf(_dmdDb2.getMaxBinaryLiteralLength()));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetMaxCharLiteralLength() {
        enter();
        try {
            println(String.valueOf(_dmdDb2.getMaxCharLiteralLength()));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

}
