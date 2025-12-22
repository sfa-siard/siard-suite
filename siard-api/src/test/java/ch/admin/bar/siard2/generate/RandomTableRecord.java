package ch.admin.bar.siard2.generate;

import ch.admin.bar.siard2.api.*;
import ch.enterag.utils.test.*;

import java.io.IOException;

public class RandomTableRecord {
    private TableRecord _tableRecord = null;

    public RandomTableRecord(TableRecord record) {
        _tableRecord = record;
    } 

    private int createValue(Value value) {
        int iReturn = RandomArchive.iRETURN_OK;
        try {
            MetaValue mv = value.getMetaValue();
            int iType = mv.getPreType();
            int iDecimals = mv.getScale();
            long lLength = mv.getMaxLength();
            if (mv.getCardinality() < 0) {
                switch (iType) {
                    case java.sql.Types.CHAR:
                        value.setString(TestUtils.getRandomFixedString((int) lLength));
                        break;
                    case java.sql.Types.VARCHAR:
                        value.setString(TestUtils.getRandomString((int) lLength));
                        break;
                    case java.sql.Types.CLOB:
                        value.setReader(new TestRandomReader(lLength));
                        break;
                    case java.sql.Types.NCHAR:
                        value.setString(TestUtils.getRandomFixedNString((int) lLength));
                        break;
                    case java.sql.Types.NVARCHAR:
                        value.setString(TestUtils.getRandomNString((int) lLength));
                        break;
                    case java.sql.Types.NCLOB:
                        value.setReader(new TestRandomNReader(lLength));
                        break;
                    case java.sql.Types.BINARY:
                        value.setBytes(TestUtils.getRandomFixedBytes((int) lLength));
                        break;
                    case java.sql.Types.VARBINARY:
                        value.setBytes(TestUtils.getRandomBytes((int) lLength));
                        break;
                    case java.sql.Types.BLOB:
                        value.setInputStream(new TestRandomInputStream(lLength));
                        break;
                    case java.sql.Types.NUMERIC:
                        value.setBigDecimal(TestUtils.getRandomBigDecimal(iDecimals));
                        break;
                    case java.sql.Types.DECIMAL:
                        value.setBigDecimal(TestUtils.getRandomBigDecimal(iDecimals));
                        break;
                    case java.sql.Types.SMALLINT:
                        value.setShort(TestUtils.getRandomShort());
                        break;
                    case java.sql.Types.INTEGER:
                        value.setInt(TestUtils.getRandomInteger());
                        break;
                    case java.sql.Types.BIGINT:
                        value.setLong(TestUtils.getRandomLong());
                        break;
                    case java.sql.Types.FLOAT:
                        value.setDouble(TestUtils.getRandomDouble());
                        break;
                    case java.sql.Types.REAL:
                        value.setFloat(TestUtils.getRandomFloat());
                        break;
                    case java.sql.Types.DOUBLE:
                        value.setDouble(TestUtils.getRandomDouble());
                        break;
                    case java.sql.Types.BOOLEAN:
                        value.setBoolean(TestUtils.getRandomBoolean());
                        break;
                    case java.sql.Types.DATE:
                        value.setDate(TestUtils.getRandomDate());
                        break;
                    case java.sql.Types.TIME:
                        value.setTime(TestUtils.getRandomTime());
                        break;
                    case java.sql.Types.TIMESTAMP:
                        value.setTimestamp(TestUtils.getRandomTimestamp());
                        break;
                    case java.sql.Types.SQLXML:
                        value.setReader(new TestRandomXmlReader(lLength));
                        break;
                    case java.sql.Types.OTHER:
                        value.setDuration(TestUtils.getRandomDuration());
                        break;
                    case java.sql.Types.NULL:
                        for (int iAttribute = 0; iAttribute < value.getAttributes(); iAttribute++) {
                            Field field = value.getAttribute(iAttribute);
                            iReturn = createValue(field);
                        }
                        break;
                }
            } else {
                // determine array size
                int iSize = Math.min(1000, mv.getCardinality());
                iSize = (int) (iSize * TestUtils.getRandomDouble());
                for (int iElement = 0; iElement < iSize; iElement++) {
                    Field field = value.getElement(iElement);
                    iReturn = createValue(field);
                }
            }
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    public int createTableRecord() {
        int iReturn = RandomArchive.iRETURN_OK;
        try {
            for (int iCell = 0; (iReturn == RandomArchive.iRETURN_OK) && (iCell < _tableRecord.getCells()); iCell++) {
                Cell cell = _tableRecord.getCell(iCell);
                iReturn = createValue(cell);
            }
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

}
