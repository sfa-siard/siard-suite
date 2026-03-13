/*======================================================================
Db2DropSchemaStatement overrides DropSchemaStatement of SQL parser.
Application : SIARD2
Description : Db2DropSchemaStatement overrides DropSchemaStatement of SQL 
              parser because DB/2 does not support drop behavior 
              (CASCADE, RESTRICT) for schemas. 
              (RESTRICT is the implicit default.) 
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 29.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.db2.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.DropSchemaStatement;
import ch.enterag.sqlparser.ddl.enums.DropBehavior;


/** Db2DropSchemaStatement overrides DropSchemaStatement of SQL parser
 * because DB/2 does not support drop behavior (CASCADE, RESTRICT) for 
 * schemas. (RESTRICT is the implicit default.)
 * @author Hartwig Thomas
 */
public class Db2DropSchemaStatement
        extends DropSchemaStatement {


    /** format the schema table statement for DB/2 without the drop behavior.
     * @return the SQL string corresponding to the fields of the drop
     *         table statement.
     */
    @Override
    public String format() {
        if (getDropBehavior() == DropBehavior.CASCADE)
            throw new IllegalArgumentException("Schema drop behavior CASCADE not supported by DB/2!");
        String sStatement = K.DROP.getKeyword() + sSP + K.SCHEMA.getKeyword() + sSP +
                getSchemaName().format() + sSP + getDropBehavior().getKeywords();
        return sStatement;
    }


    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
    public Db2DropSchemaStatement(SqlFactory sf) {
        super(sf);
    }

}
