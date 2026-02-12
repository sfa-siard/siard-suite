/*======================================================================
OracleSqlParameterDeclaration overrides SqlParameterDeclaration of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleSqlParameterDeclaration overrides SqlParameterDeclaration
			  of SQL parser.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.SqlParameterDeclaration;

/*====================================================================*/
/** OracleSqlParameterDeclaration overrides the formatting
 * of the SqlParameterDeclaration
 * @author Simon Jutz
 *
 */
public class OracleSqlParameterDeclaration
	extends SqlParameterDeclaration
{
    /*------------------------------------------------------------------*/
	/**
	 * format the sql parameter declaration
	 * @return the SQL string corresponding to a sql parameter declaration
	 */
	@Override
	public String format() {
	    String sDeclaration = "";
	    if (getParameterName().isSet())
	      sDeclaration = sDeclaration + getParameterName().quote();
	    if (getParameterMode() != null)
		      sDeclaration = getParameterMode().getKeywords() + sSP;
	    if (getDataType() != null)
	      sDeclaration = sDeclaration + sSP + getDataType().format();
	    
	    return sDeclaration;
	} /* format */

    /*------------------------------------------------------------------*/
    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
	public OracleSqlParameterDeclaration(SqlFactory sf) {
		super(sf);
	} /* constructor */
}
