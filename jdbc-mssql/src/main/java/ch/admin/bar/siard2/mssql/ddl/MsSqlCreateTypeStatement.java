/*======================================================================
MsSqlCreateTypeStatement overrides CreateTypeStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : MsSqlCreateTypeStatement overrides CreateTypeStatement of SQL 
              parser because MSSQL only supports DISTINCT types and 
              uses another syntax for their creation.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.mssql.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.CreateTypeStatement;


/** MsSqlCreateTypeStatement overrides CreateTypeStatement of SQL parser
 * because MSSQL only supports DISTINCT types and uses another syntax 
 * for their creation.
 * @author Hartwig Thomas
 */
public class MsSqlCreateTypeStatement
        extends CreateTypeStatement {


    /** format the create type statement.
     * @return the SQL string corresponding to the fields of the create
     *   type statement.
     */
    @Override
    public String format() {
        String sStatement = null;
        sStatement = K.CREATE.getKeyword() + sSP + K.TYPE.getKeyword() + sSP +
                getTypeName().format();
        if (getDistinctBaseType() != null)
            sStatement = sStatement + sSP + K.FROM.getKeyword() + sSP + getDistinctBaseType().format();
        else
            throw new IllegalArgumentException("MS SQL Server only supports DISTINCT types!");
        return sStatement;
    }


    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
    public MsSqlCreateTypeStatement(SqlFactory sf) {
        super(sf);
    }

}
