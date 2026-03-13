/*======================================================================
MsSqlDataType implements the type translation from ISO SQL to MSSQL for 
              complex types.
Version     : $Id: $
Application : SIARD2
Description : MsSqlDataType implements the type translation from ISO 
              SQL:2008 to MSSQL for complex types.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 14.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.mssql.datatype;

import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.PredefinedType;


/** MsSqlDataType implements the type translation from ISO SQL to MSSQL
 * for complex types.
 * @author Hartwig Thomas
 */
public class MsSqlDataType
        extends DataType {


    /** format an ARRAY type.
     * In MSSQL an ARRAY is serialized to as a BLOB.
     * @return SQL for ARRAY type.
     */
    @Override
    protected String formatArrayType() {
        PredefinedType pt = getSqlFactory().newPredefinedType();
        pt.initBlobType(PredefinedType.iUNDEFINED, null);
        return pt.format();
    }


    /** format the data type for MSSQL.
     * @return the SQL string corresponding to the fields of the data type.
     */
    @Override
    public String format() {
        String sDataType = null;
        switch (getType()) {
            case PRE:
                sDataType = getPredefinedType().format();
                break;
            case STRUCT:
                sDataType = formatStructType();
                break;
            case ROW:
                sDataType = formatRowType();
                break;
            case REF:
                sDataType = formatRefType();
                break;
            case ARRAY:
                sDataType = formatArrayType();
                break;
            case MULTISET:
                sDataType = formatMultisetType();
                break;
        }
        return sDataType;
    }


    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
    public MsSqlDataType(SqlFactory sf) {
        super(sf);
    }

}
