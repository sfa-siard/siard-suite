/*======================================================================
OracleUnsignedLiteral implements the value translation from ISO SQL to Oracle.
Version     : $Id: $
Application : SIARD2
Description : OracleLiteral implements the value translation from 
              ISO SQL:2008 to Oracle.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rueti ZH, Switzerland
Created    : 28.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.expression;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.admin.bar.siard2.oracle.*;

/*====================================================================*/
/** OracleUnsignedLiteral implements the value translation from ISO SQL:2008
 * to Oracle
 * @author Simon Jutz
 */
public class OracleUnsignedLiteral 
	extends UnsignedLiteral
{
	@Override
	public String format()
	{
		String sFormatted = "";
		if (getBytes() != null) {
			sFormatted = OracleLiterals.formatBytesLiteral(getBytes());
		} else if (getBoolean() != null) {
			sFormatted = OracleLiterals.formatBooleanLiteral(getBoolean());
		}  else if (getTime() != null) {
	      sFormatted = OracleLiterals.formatTimeLiteral(getTime());
		} else {
			sFormatted = super.format();
		}
		return sFormatted;
	} /* format */
	
	public OracleUnsignedLiteral(SqlFactory sf) {
		super(sf);
	} /* constructor */

} /* class OracleUnsignedLiteral */