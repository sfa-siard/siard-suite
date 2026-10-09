package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.db2.TestDb2Database;
import ch.admin.bar.siard2.db2.TestSqlDatabase;
import ch.admin.bar.siard2.db2.datatype.Db2PredefinedType;
import ch.admin.bar.siard2.jdbcx.Db2DataSource;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.database.SqlTypes;
import ch.enterag.utils.jdbc.BaseDatabaseMetaDataTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Db2Container;

import java.sql.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class Db2DatabaseMetaDataTester extends BaseDatabaseMetaDataTester {

    @Container
    public static Db2Container db2 = new Db2Container("ibmcom/db2:11.5.7.0").acceptLicense();

    private static final String TESTUSER = "TESTUSER";
    private static Pattern _patTYPE = Pattern.compile("^(.*?)(\\(\\s*((\\d+)(\\s*,\\s*(\\d+))?)\\s*\\))?$");

    private Db2DatabaseMetaData _dmdDb2 = null;

    @BeforeAll
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
    }

    @BeforeEach
    public void setUp() throws SQLException {
        Db2DataSource dsDb2 = new Db2DataSource();
        dsDb2.setUrl(db2.getJdbcUrl());
        dsDb2.setUser(db2.getUsername());
        dsDb2.setPassword(db2.getPassword());
        Connection conn = dsDb2.getConnection();
        conn.setAutoCommit(false);
        _dmdDb2 = (Db2DatabaseMetaData) conn.getMetaData();
        setDatabaseMetaData(_dmdDb2);
    }

    @Test
    public void testClass() {
        assertEquals(Db2DatabaseMetaData.class, _dmdDb2.getClass(), "Wrong database meta data class!");
    }

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
                        assertEquals(iDataType, Types.CHAR, "Invalid CHAR mapping!");
                        break;
                    case "VARCHAR":
                        assertEquals(iDataType, Types.VARCHAR, "Invalid VARCHAR mapping!");
                        break;
                    case "CLOB":
                        assertEquals(iDataType, Types.CLOB, "Invalid CLOB mapping!");
                        break;
                    case "GRAPHIC":
                        assertEquals(iDataType, Types.NCHAR, "Invalid GRAPHIC mapping!");
                        break;
                    case "VARGRAPHIC":
                        assertEquals(iDataType, Types.NVARCHAR, "Invalid VARGRAPHIC mapping!");
                        break;
                    case "DBCLOB":
                        assertEquals(iDataType, Types.NCLOB, "Invalid DBCLOB mapping!");
                        break;
                    case "CHAR () FOR BIT DATA":
                        assertEquals(iDataType, Types.BINARY, "Invalid CHAR() FOR BIT DATA mapping!");
                        break;
                    case "BINARY":
                        assertEquals(iDataType, Types.BINARY, "Invalid BINARY mapping!");
                        break;
                    case "VARBINARY":
                        assertEquals(iDataType, Types.VARBINARY, "Invalid VARBINARY mapping!");
                        break;
                    case "BLOB":
                        assertEquals(iDataType, Types.BLOB, "Invalid BLOB mapping!");
                        break;
                    case "SMALLINT":
                        assertEquals(iDataType, Types.SMALLINT, "Invalid SMALLINT mapping!");
                        break;
                    case "INTEGER":
                        assertEquals(iDataType, Types.INTEGER, "Invalid INTEGER mapping!");
                        break;
                    case "BIGINT":
                        assertEquals(iDataType, Types.BIGINT, "Invalid BIGINT mapping!");
                        break;
                    case "DECIMAL":
                        assertEquals(iDataType, Types.DECIMAL, "Invalid DECIMAL mapping!");
                        break;
                    case "DECFLOAT":
                        assertEquals(iDataType, Types.DECIMAL, "Invalid DECFLOAT mapping!");
                        break;
                    case "REAL":
                        assertEquals(iDataType, Types.REAL, "Invalid REAL mapping!");
                        break;
                    case "DOUBLE":
                        assertEquals(iDataType, Types.DOUBLE, "Invalid DOUBLE mapping!");
                        break;
                    case "DATE":
                        assertEquals(iDataType, Types.DATE, "Invalid DATE mapping!");
                        break;
                    case "TIME":
                        assertEquals(iDataType, Types.TIME, "Invalid TIME mapping!");
                        break;
                    case "TIMESTAMP":
                        assertEquals(iDataType, Types.TIMESTAMP, "Invalid TIMESTAMP mapping!");
                        break;
                    case "XML":
                        assertEquals(iDataType, Types.SQLXML, "Invalid XML mapping!");
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
                        assertEquals(iPrecision, iColumnSize, "Explicit precision does not match!");
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
    }

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
                        assertEquals(iDataType, Types.CHAR, "Invalid CHAR mapping!");
                        break;
                    case "VARCHAR":
                        assertEquals(iDataType, Types.VARCHAR, "Invalid VARCHAR mapping!");
                        break;
                    case "CLOB":
                        assertEquals(iDataType, Types.CLOB, "Invalid CLOB mapping!");
                        break;
                    case "GRAPHIC":
                        assertEquals(iDataType, Types.NCHAR, "Invalid GRAPHIC mapping!");
                        break;
                    case "VARGRAPHIC":
                        assertEquals(iDataType, Types.NVARCHAR, "Invalid VARGRAPHIC mapping!");
                        break;
                    case "DBCLOB":
                        assertEquals(iDataType, Types.NCLOB, "Invalid DBCLOB mapping!");
                        break;
                    case "CHAR () FOR BIT DATA":
                        assertEquals(iDataType, Types.BINARY, "Invalid CHAR() FOR BIT DATA mapping!");
                        break;
                    case "BINARY":
                        assertEquals(iDataType, Types.BINARY, "Invalid BINARY mapping!");
                        break;
                    case "VARBINARY":
                        assertEquals(iDataType, Types.VARBINARY, "Invalid VARCHAR FOR BIT DATA mapping!");
                        break;
                    case "BLOB":
                        assertEquals(iDataType, Types.BLOB, "Invalid BLOB mapping!");
                        break;
                    case "SMALLINT":
                        assertEquals(iDataType, Types.SMALLINT, "Invalid SMALLINT mapping!");
                        break;
                    case "INTEGER":
                        assertEquals(iDataType, Types.INTEGER, "Invalid INTEGER mapping!");
                        break;
                    case "BIGINT":
                        assertEquals(iDataType, Types.BIGINT, "Invalid BIGINT mapping!");
                        break;
                    case "DECIMAL":
                        assertEquals(iDataType, Types.DECIMAL, "Invalid DECIMAL mapping!");
                        break;
                    case "DECFLOAT":
                        assertEquals(iDataType, Types.DECIMAL, "Invalid DECFLOAT mapping!");
                        break;
                    case "REAL":
                        assertEquals(iDataType, Types.REAL, "Invalid REAL mapping!");
                        break;
                    case "DOUBLE":
                        assertEquals(iDataType, Types.DOUBLE, "Invalid DOUBLE mapping!");
                        break;
                    case "DATE":
                        assertEquals(iDataType, Types.DATE, "Invalid DATE mapping!");
                        break;
                    case "TIME":
                        assertEquals(iDataType, Types.TIME, "Invalid TIME mapping!");
                        break;
                    case "TIMESTAMP":
                        assertEquals(iDataType, Types.TIMESTAMP, "Invalid TIMESTAMP mapping!");
                        break;
                    case "XML":
                        assertEquals(iDataType, Types.SQLXML, "Invalid XML mapping!");
                        break;
                    case "INTERVAL YEAR TO MONTH":
                        assertEquals(iDataType, Types.OTHER, "Invalid INTERVAL mapping!");
                        break;
                    case "INTERVAL DAY TO SECOND":
                        assertEquals(iDataType, Types.OTHER, "Invalid INTERVAL mapping!");
                        break;
                    default:
                        assertEquals(iDataType, Types.DISTINCT, "Invalid DISTINCT mapping!");
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
                        assertEquals(iPrecision, iColumnSize, "Explicit precision does not match!");
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
    }

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
                            assertEquals(iDataType, Types.CHAR, "Invalid CHAR mapping!");
                            break;
                        case "VARCHAR":
                            assertEquals(iDataType, Types.VARCHAR, "Invalid VARCHAR mapping!");
                            break;
                        case "CLOB":
                            assertEquals(iDataType, Types.CLOB, "Invalid CLOB mapping!");
                            break;
                        case "GRAPHIC":
                            assertEquals(iDataType, Types.NCHAR, "Invalid GRAPHIC mapping!");
                            break;
                        case "VARGRAPHIC":
                            assertEquals(iDataType, Types.NVARCHAR, "Invalid VARGRAPHIC mapping!");
                            break;
                        case "DBCLOB":
                            assertEquals(iDataType, Types.NCLOB, "Invalid DBCLOB mapping!");
                            break;
                        case "CHAR () FOR BIT DATA":
                            assertEquals(iDataType, Types.BINARY, "Invalid CHAR() FOR BIT DATA mapping!");
                            break;
                        case "BINARY":
                            assertEquals(iDataType, Types.BINARY, "Invalid BINARY mapping!");
                            break;
                        case "VARCHAR () FOR BIT DATA":
                            assertEquals(iDataType, Types.VARBINARY, "Invalid VARCHAR FOR BIT DATA mapping!");
                            break;
                        case "BLOB":
                            assertEquals(iDataType, Types.BLOB, "Invalid BLOB mapping!");
                            break;
                        case "SMALLINT":
                            assertEquals(iDataType, Types.SMALLINT, "Invalid SMALLINT mapping!");
                            break;
                        case "INTEGER":
                            assertEquals(iDataType, Types.INTEGER, "Invalid INTEGER mapping!");
                            break;
                        case "BIGINT":
                            assertEquals(iDataType, Types.BIGINT, "Invalid BIGINT mapping!");
                            break;
                        case "DECIMAL":
                            assertEquals(iDataType, Types.DECIMAL, "Invalid DECIMAL mapping!");
                            break;
                        case "DECFLOAT":
                            assertEquals(iDataType, Types.FLOAT, "Invalid DECFLOAT mapping!");
                            break;
                        case "REAL":
                            assertEquals(iDataType, Types.REAL, "Invalid REAL mapping!");
                            break;
                        case "DOUBLE":
                            assertEquals(iDataType, Types.DOUBLE, "Invalid DOUBLE mapping!");
                            break;
                        case "DATE":
                            assertEquals(iDataType, Types.DATE, "Invalid DATE mapping!");
                            break;
                        case "TIME":
                            assertEquals(iDataType, Types.TIME, "Invalid TIME mapping!");
                            break;
                        case "TIMESTAMP":
                            assertEquals(iDataType, Types.TIMESTAMP, "Invalid TIMESTAMP mapping!");
                            break;
                        case "XML":
                            assertEquals(iDataType, Types.SQLXML, "Invalid XML mapping!");
                            break;
                        default:
                            try {
                                QualifiedId qiType = new QualifiedId(sTypeName);
                                if (qiType.getName()
                                          .equals(Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE) || qiType.getName()
                                                                                                        .equals(Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE))
                                    assertEquals(iDataType, Types.OTHER, "Invalid INTERVAL mapping!");
                                else assertEquals(iDataType, Types.DISTINCT, "Invalid DISTINCT mapping!");
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
                            assertEquals(iPrecision, iColumnSize, "Explicit precision does not match!");
                        }
                    }
                } else {
                    TestColumnDefinition tcd = mapCd.get(sColumnName);
                    try {
                        QualifiedId qiTypeName = new QualifiedId(sTypeName);
                        assertEquals(tcd.getType(), qiTypeName.format(), "Invalid UDT type!");
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
    }

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
                            assertEquals(iDataType, Types.CHAR, "Invalid CHAR mapping!");
                            break;
                        case "VARCHAR":
                            assertEquals(iDataType, Types.VARCHAR, "Invalid VARCHAR mapping!");
                            break;
                        case "CLOB":
                            assertEquals(iDataType, Types.CLOB, "Invalid CLOB mapping!");
                            break;
                        case "GRAPHIC":
                            assertEquals(iDataType, Types.NCHAR, "Invalid GRAPHIC mapping!");
                            break;
                        case "VARGRAPHIC":
                            assertEquals(iDataType, Types.NVARCHAR, "Invalid VARGRAPHIC mapping!");
                            break;
                        case "DBCLOB":
                            assertEquals(iDataType, Types.NCLOB, "Invalid DBCLOB mapping!");
                            break;
                        case "CHAR () FOR BIT DATA":
                            assertEquals(iDataType, Types.BINARY, "Invalid CHAR() FOR BIT DATA mapping!");
                            break;
                        case "BINARY":
                            assertEquals(iDataType, Types.BINARY, "Invalid BINARY mapping!");
                            break;
                        case "VARCHAR () FOR BIT DATA":
                            assertEquals(iDataType, Types.VARBINARY, "Invalid VARCHAR FOR BIT DATA mapping!");
                            break;
                        case "BLOB":
                            assertEquals(iDataType, Types.BLOB, "Invalid BLOB mapping!");
                            break;
                        case "SMALLINT":
                            assertEquals(iDataType, Types.SMALLINT, "Invalid SMALLINT mapping!");
                            break;
                        case "INTEGER":
                            assertEquals(iDataType, Types.INTEGER, "Invalid INTEGER mapping!");
                            break;
                        case "BIGINT":
                            assertEquals(iDataType, Types.BIGINT, "Invalid BIGINT mapping!");
                            break;
                        case "DECIMAL":
                            assertEquals(iDataType, Types.DECIMAL, "Invalid DECIMAL mapping!");
                            break;
                        case "DECFLOAT":
                            assertEquals(iDataType, Types.FLOAT, "Invalid DECFLOAT mapping!");
                            break;
                        case "REAL":
                            assertEquals(iDataType, Types.REAL, "Invalid REAL mapping!");
                            break;
                        case "DOUBLE":
                            assertEquals(iDataType, Types.DOUBLE, "Invalid DOUBLE mapping!");
                            break;
                        case "DATE":
                            assertEquals(iDataType, Types.DATE, "Invalid DATE mapping!");
                            break;
                        case "TIME":
                            assertEquals(iDataType, Types.TIME, "Invalid TIME mapping!");
                            break;
                        case "TIMESTAMP":
                            assertEquals(iDataType, Types.TIMESTAMP, "Invalid TIMESTAMP mapping!");
                            break;
                        case "XML":
                            assertEquals(iDataType, Types.SQLXML, "Invalid XML mapping!");
                            break;
                        default:
                            try {
                                QualifiedId qiType = new QualifiedId(sTypeName);
                                if (qiType.getName()
                                          .equals(Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE) || qiType.getName()
                                                                                                        .equals(Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE))
                                    assertEquals(iDataType, Types.OTHER, "Invalid INTERVAL mapping!");
                                else assertEquals(iDataType, Types.DISTINCT, "Invalid DISTINCT mapping!");
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
                            assertEquals(iPrecision, iColumnSize, "Explicit precision does not match!");
                        }
                    }
                } else {
                    TestColumnDefinition tcd = mapCd.get(sColumnName);
                    try {
                        QualifiedId qiTypeName = new QualifiedId(sTypeName);
                        assertEquals(tcd.getType(), qiTypeName.format(), "Invalid UDT type!");
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
    }

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
                assertEquals(TestDb2Database.getQualifiedStructType()
                                                              .getCatalog(), sCatalog, "Wrong catalog!");
                String sSchema = rs.getString("TYPE_SCHEM");
                assertEquals(TestDb2Database.getQualifiedStructType()
                                                             .getSchema(), sSchema, "Wrong schema!");
                String sTypeName = rs.getString("TYPE_NAME");
                assertEquals(TestDb2Database.getQualifiedStructType()
                                                           .getName(), sTypeName, "Wrong type!");
                String sAttrName = rs.getString("ATTR_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                String sAttrTypeName = rs.getString("ATTR_TYPE_NAME");
                int iAttrSize = rs.getInt("ATTR_SIZE");
                if ((iDataType != Types.DISTINCT) && (iDataType != Types.STRUCT)) {
                    switch (sAttrTypeName) {
                        case "CHAR":
                            assertEquals(iDataType, Types.CHAR, "Invalid CHAR mapping!");
                            break;
                        case "VARCHAR":
                            assertEquals(iDataType, Types.VARCHAR, "Invalid VARCHAR mapping!");
                            break;
                        case "CLOB":
                            assertEquals(iDataType, Types.CLOB, "Invalid CLOB mapping!");
                            break;
                        case "GRAPHIC":
                            assertEquals(iDataType, Types.NCHAR, "Invalid GRAPHIC mapping!");
                            break;
                        case "VARGRAPHIC":
                            assertEquals(iDataType, Types.NVARCHAR, "Invalid VARGRAPHIC mapping!");
                            break;
                        case "DBCLOB":
                            assertEquals(iDataType, Types.NCLOB, "Invalid DBCLOB mapping!");
                            break;
                        case "CHAR () FOR BIT DATA":
                            assertEquals(iDataType, Types.BINARY, "Invalid CHAR() FOR BIT DATA mapping!");
                            break;
                        case "BINARY":
                            assertEquals(iDataType, Types.BINARY, "Invalid BINARY mapping!");
                            break;
                        case "VARCHAR () FOR BIT DATA":
                            assertEquals(iDataType, Types.VARBINARY, "Invalid VARCHAR FOR BIT DATA mapping!");
                            break;
                        case "BLOB":
                            assertEquals(iDataType, Types.BLOB, "Invalid BLOB mapping!");
                            break;
                        case "SMALLINT":
                            assertEquals(iDataType, Types.SMALLINT, "Invalid SMALLINT mapping!");
                            break;
                        case "INTEGER":
                            assertEquals(iDataType, Types.INTEGER, "Invalid INTEGER mapping!");
                            break;
                        case "BIGINT":
                            assertEquals(iDataType, Types.BIGINT, "Invalid BIGINT mapping!");
                            break;
                        case "DECIMAL":
                            assertEquals(iDataType, Types.DECIMAL, "Invalid DECIMAL mapping!");
                            break;
                        case "DECFLOAT":
                            assertEquals(iDataType, Types.FLOAT, "Invalid DECFLOAT mapping!");
                            break;
                        case "REAL":
                            assertEquals(iDataType, Types.REAL, "Invalid REAL mapping!");
                            break;
                        case "DOUBLE":
                            assertEquals(iDataType, Types.DOUBLE, "Invalid DOUBLE mapping!");
                            break;
                        case "DATE":
                            assertEquals(iDataType, Types.DATE, "Invalid DATE mapping!");
                            break;
                        case "TIME":
                            assertEquals(iDataType, Types.TIME, "Invalid TIME mapping!");
                            break;
                        case "TIMESTAMP":
                            assertEquals(iDataType, Types.TIMESTAMP, "Invalid TIMESTAMP mapping!");
                            break;
                        case "XML":
                            assertEquals(iDataType, Types.SQLXML, "Invalid XML mapping!");
                            break;
                        default:
                            try {
                                QualifiedId qiType = new QualifiedId(sTypeName);
                                if (qiType.getName()
                                          .equals(Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE) || qiType.getName()
                                                                                                        .equals(Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE))
                                    assertEquals(iDataType, Types.OTHER, "Invalid INTERVAL mapping!");
                                else assertEquals(iDataType, Types.DISTINCT, "Invalid DISTINCT mapping!");
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
                            assertEquals(iPrecision, iAttrSize, "Explicit precision does not match!");
                        }
                    }
                } else {
                    TestColumnDefinition tad = mapAd.get(sAttrName);
                    try {
                        QualifiedId qiTypeName = new QualifiedId(sAttrTypeName);
                        assertEquals(tad.getType(), qiTypeName.format(), "Invalid UDT type!");
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
                assertEquals(TestSqlDatabase.getQualifiedComplexType()
                                                              .getCatalog(), sCatalog, "Wrong catalog!");
                String sSchema = rs.getString("TYPE_SCHEM");
                assertEquals(TestSqlDatabase.getQualifiedComplexType()
                                                             .getSchema(), sSchema, "Wrong schema!");
                String sTypeName = rs.getString("TYPE_NAME");
                assertEquals(TestSqlDatabase.getQualifiedComplexType()
                                                           .getName(), sTypeName, "Wrong type!");
                String sAttrName = rs.getString("ATTR_NAME");
                int iDataType = rs.getInt("DATA_TYPE");
                String sAttrTypeName = rs.getString("ATTR_TYPE_NAME");
                int iAttrSize = rs.getInt("ATTR_SIZE");
                if ((iDataType != Types.DISTINCT) && (iDataType != Types.STRUCT)) {
                    switch (sAttrTypeName) {
                        case "CHAR":
                            assertEquals(iDataType, Types.CHAR, "Invalid CHAR mapping!");
                            break;
                        case "VARCHAR":
                            assertEquals(iDataType, Types.VARCHAR, "Invalid VARCHAR mapping!");
                            break;
                        case "CLOB":
                            assertEquals(iDataType, Types.CLOB, "Invalid CLOB mapping!");
                            break;
                        case "GRAPHIC":
                            assertEquals(iDataType, Types.NCHAR, "Invalid GRAPHIC mapping!");
                            break;
                        case "VARGRAPHIC":
                            assertEquals(iDataType, Types.NVARCHAR, "Invalid VARGRAPHIC mapping!");
                            break;
                        case "DBCLOB":
                            assertEquals(iDataType, Types.NCLOB, "Invalid DBCLOB mapping!");
                            break;
                        case "CHAR () FOR BIT DATA":
                            assertEquals(iDataType, Types.BINARY, "Invalid CHAR() FOR BIT DATA mapping!");
                            break;
                        case "BINARY":
                            assertEquals(iDataType, Types.BINARY, "Invalid BINARY mapping!");
                            break;
                        case "VARCHAR () FOR BIT DATA":
                            assertEquals(iDataType, Types.VARBINARY, "Invalid VARCHAR FOR BIT DATA mapping!");
                            break;
                        case "BLOB":
                            assertEquals(iDataType, Types.BLOB, "Invalid BLOB mapping!");
                            break;
                        case "SMALLINT":
                            assertEquals(iDataType, Types.SMALLINT, "Invalid SMALLINT mapping!");
                            break;
                        case "INTEGER":
                            assertEquals(iDataType, Types.INTEGER, "Invalid INTEGER mapping!");
                            break;
                        case "BIGINT":
                            assertEquals(iDataType, Types.BIGINT, "Invalid BIGINT mapping!");
                            break;
                        case "DECIMAL":
                            assertEquals(iDataType, Types.DECIMAL, "Invalid DECIMAL mapping!");
                            break;
                        case "DECFLOAT":
                            assertEquals(iDataType, Types.FLOAT, "Invalid DECFLOAT mapping!");
                            break;
                        case "REAL":
                            assertEquals(iDataType, Types.REAL, "Invalid REAL mapping!");
                            break;
                        case "DOUBLE":
                            assertEquals(iDataType, Types.DOUBLE, "Invalid DOUBLE mapping!");
                            break;
                        case "DATE":
                            assertEquals(iDataType, Types.DATE, "Invalid DATE mapping!");
                            break;
                        case "TIME":
                            assertEquals(iDataType, Types.TIME, "Invalid TIME mapping!");
                            break;
                        case "TIMESTAMP":
                            assertEquals(iDataType, Types.TIMESTAMP, "Invalid TIMESTAMP mapping!");
                            break;
                        case "XML":
                            assertEquals(iDataType, Types.SQLXML, "Invalid XML mapping!");
                            break;
                        default:
                            try {
                                QualifiedId qiType = new QualifiedId(sTypeName);
                                if (qiType.getName()
                                          .equals(Db2PredefinedType.sYEAR_MONTH_DISTINCT_TYPE) || qiType.getName()
                                                                                                        .equals(Db2PredefinedType.sDAY_SECOND_DISTINCT_TYPE))
                                    assertEquals(iDataType, Types.OTHER, "Invalid INTERVAL mapping!");
                                else assertEquals(iDataType, Types.DISTINCT, "Invalid DISTINCT mapping!");
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
                            assertEquals(iPrecision, iAttrSize, "Explicit precision does not match!");
                        }
                    }
                } else {
                    TestColumnDefinition tad = mapAd.get(sAttrName);
                    try {
                        QualifiedId qiTypeName = new QualifiedId(sAttrTypeName);
                        assertEquals(tad.getType(), qiTypeName.format(), "Invalid UDT type!");
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
