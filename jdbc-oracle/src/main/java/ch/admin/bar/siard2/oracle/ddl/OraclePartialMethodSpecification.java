/*======================================================================
OraclePartialMethodSpecification overrides PartialMethodSpecification of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleMethodSpecification overrides PartialMethodSpecification
			  of SQL parser, because Oracle has limited method 
			  characteristics and simplified parameters
			  see: https://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_8001.htm#i2126584
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.PartialMethodSpecification;
import ch.enterag.sqlparser.ddl.enums.MethodType;

/*====================================================================*/
public class OraclePartialMethodSpecification
	extends PartialMethodSpecification 
{
    /*------------------------------------------------------------------*/
	/**
	 * format the partial method specification
	 * @return the SQL string corresponding to a partial method specification
	 */
	@Override
	public String format() 
	{
	    String sSpecification = "";
	    if (getMethodType() != null) {
	    	if (getMethodType().equals(MethodType.INSTANCE)) {
	    		sSpecification = sSpecification + K.MEMBER.getKeyword() + sSP;
	    	} else {
	    		sSpecification = sSpecification + getMethodType().getKeywords() + sSP;
	    	}
	    }
	    
	    sSpecification = sSpecification + K.FUNCTION.getKeyword() + sSP;
	    
	    // Oracle Parameters do not have a mode!
	    sSpecification = sSpecification + formatSqlParameterDeclarations();
	    
	    if (hasReturnsType()) {
	    	sSpecification = sSpecification + sSP + K.RETURN.getKeyword();
	    	if (getReturnsType() != null)  {
	    		sSpecification = sSpecification + sSP + getReturnsType().format();
	    	}
	    }
	    return sSpecification;	
	} /* format */

    /*------------------------------------------------------------------*/
	/**
	 * format the method parameters
	 * @return the SQL string corresponding to the methods parameters
	 */
	private String formatSqlParameterDeclarations() {
	    String sList = sLEFT_PAREN;
	    for (int iParameter = 0; iParameter < getSqlParameterDeclarations().size(); iParameter++)
	    {
	      if (iParameter > 0)
	        sList = sList + sCOMMA;
	      
	      sList = sList + sNEW_LINE + sINDENT + 
	    		  getSqlParameterDeclarations().get(iParameter).getDataType().format() + sSP + 
	    		  getSqlParameterDeclarations().get(iParameter).getParameterName().format();
	    }
	    if (getSqlParameterDeclarations().size() > 0)
	      sList = sList + sNEW_LINE;
	    sList = sList + sRIGHT_PAREN;
	    return sList;
	} /* formatSqlParameterDeclarations */

    /*------------------------------------------------------------------*/
    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
	public OraclePartialMethodSpecification(SqlFactory sf) {
		super(sf);
	} /* constructor */
	
}
