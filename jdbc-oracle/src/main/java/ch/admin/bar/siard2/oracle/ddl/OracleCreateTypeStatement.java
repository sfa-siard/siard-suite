/*======================================================================
OracleCreateTypeStatement overrides CreateTypeStatement of SQL parser.
Version     : $Id: $
Application : SIARD2
Description : OracleCreateTypeStatement overrides CreateTypeStatement
			  of SQL parser, because Oracle's CREATE TYPE syntax does
			  not follow the SQL specification
			  see: https://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_8001.htm#i2126584
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 20.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.ddl.CreateTypeStatement;

/*====================================================================*/
/** OracleCreateTypeStatement overrides CreateTypeStatement of SQL parser.
 * @author Simon Jutz
 */
public class OracleCreateTypeStatement 
	extends CreateTypeStatement
{
    /*------------------------------------------------------------------*/
	/**
	 * format the create type statement
	 * @return the SQL string corresponding to a create type statement
	 */
	@Override
	public String format()
	{
		/* Incomplete Type */
		String sStatement = K.CREATE.getKeyword() + sSP + K.TYPE.getKeyword() + sSP + 
				getTypeName().quote();
		
		if (getDistinctBaseType() != null) {
			/* Distinct Type */
			sStatement = sStatement + sSP + K.AS.getKeyword() + sSP + getDistinctBaseType().format();
		} else {
			/* Object Type */
			if (getSuperType().isSet()) {
				sStatement = sStatement + sSP + K.UNDER.getKeyword() + sSP + getSuperType().quote();
			} else {
				sStatement = sStatement + sSP + K.AS.getKeyword() + sSP + K.OBJECT.getKeyword();
			}
			if (getAttributeDefinitions().size() + getMethodSpecifications().size() > 0) {
				sStatement = sStatement + sLEFT_PAREN;
				for (int iDefinition = 0; iDefinition < getAttributeDefinitions().size(); iDefinition++) {
					if (iDefinition > 0) 
						sStatement = sStatement + sCOMMA;
					sStatement = sStatement + sNEW_LINE + sINDENT + getAttributeDefinitions().get(iDefinition).format();
				}
				for (int iSpecification = 0; iSpecification < getMethodSpecifications().size(); iSpecification++) {
					if (getAttributeDefinitions().size() > 0 || iSpecification > 0)
						sStatement = sStatement + sCOMMA;
					sStatement = sStatement + sNEW_LINE + getMethodSpecifications().get(iSpecification).format(); 
				}
				sStatement = sStatement + sNEW_LINE + sRIGHT_PAREN;
			}
			if (getInstantiability() != null) {
				sStatement = sStatement + sSP + getInstantiability().getKeywords();
			}
			if (getFinality() != null) {
				sStatement = sStatement + sSP + getFinality().getKeywords();
			}
		}
		
		return sStatement;
	}

    /*------------------------------------------------------------------*/
    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
	public OracleCreateTypeStatement(SqlFactory sf) {
		super(sf);
	} /* constructor */

}
