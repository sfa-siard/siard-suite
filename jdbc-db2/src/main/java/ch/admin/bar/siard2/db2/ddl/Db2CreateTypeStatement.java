/*======================================================================
Db2CreateTypeStatement overrides CreateTypeStatement of SQL parser.
Application : SIARD2
Description : Db2CreateTypeStatement overrides CreateTypeStatement of SQL 
              parser because DB/2 requires MODE DB2SQL. 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 29.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.db2.ddl;

import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.CreateTypeStatement;


/** Db2CreateTypeStatement overrides CreateTypeStatement of SQL parser
 * because DB/2 requires MODE DB2SQL.
 * @author Hartwig Thomas
 */
public class Db2CreateTypeStatement
        extends CreateTypeStatement {


    /** format the create type statement.
     * @return the SQL string corresponding to the fields of the create
     *   type statement.
     */
    @Override
    public String format() {
        String sStatement = super.format();
        if (getDistinctBaseType() == null)
            sStatement = sStatement + sSP + "MODE" + sSP + "DB2SQL";
        return sStatement;
    }


    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
    public Db2CreateTypeStatement(SqlFactory sf) {
        super(sf);
    }

}
