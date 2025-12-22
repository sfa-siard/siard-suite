/*======================================================================
AccessConverter implements basic type and value conversions between SQL
and Jackcess.
Application : Access JDBC driver
Description : AccessConverter implements basic type and value conversions 
              between SQL and Jackcess.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 07.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.access;


/** AccessConverter implements basic type and value conversions between
 * SQL and Jackcess.
 * @author Hartwig Thomas
 */
abstract public class AccessConverter {
  /*====================================================================
  SQL 
  ====================================================================*/

    /** convert native SQL to standard SQL
     * replacing all bracketed identifiers by quoted identifiers,
     * duplicating quotes inside them and
     * replacing all strings in double quotes by strings in single quotes.
     * Further standardization will have to be handled using the SQL parser.
     * @param sNativeSql native SQL.
     * @return standard SQL.
     */
    public static String standardSQL(String sNativeSql) {
        /* just replace all bracketed identifiers by quotes, duplicating
         * quotes inside them */
        StringBuilder sbStandard = new StringBuilder();
        boolean bSingleQuoted = false;
        boolean bDoubleQuoted = false;
        boolean bIdQuoted = false;
        /* detach trailing ";" */
        int iLength = sNativeSql.length();
        if (sNativeSql.endsWith(";"))
            iLength--;
        for (int i = 0; i < iLength; i++) {
            char c = sNativeSql.charAt(i);
            if ((!bSingleQuoted) && (!bDoubleQuoted) && (!bIdQuoted) && (c == '[')) {
                bIdQuoted = true;
                sbStandard.append("\"");
            } else if (bIdQuoted && (c == ']')) {
                /* two closing brackets need to become a single one belonging to the identifier */
                if ((i >= iLength - 1) || (sNativeSql.charAt(i + 1) != ']')) {
                    sbStandard.append('\"');
                    bIdQuoted = false;
                } else {
                    sbStandard.append(c);
                    i++;
                }
            } else if (bIdQuoted && (c == '\"')) {
                /* duplicate double quotes */
                sbStandard.append(c);
                sbStandard.append(c);
            } else if ((!bIdQuoted) && (!bDoubleQuoted) && (c == '\'')) {
                sbStandard.append(c);
                if (!bSingleQuoted)
                    bSingleQuoted = true;
                else {
                    /* two single quotes represent a single one */
                    if ((i >= iLength - 1) || (sNativeSql.charAt(i + 1) != '\''))
                        bSingleQuoted = false;
                    else {
                        sbStandard.append(c);
                        i++;
                    }
                }
            } else if ((!bIdQuoted) && (!bSingleQuoted) && (c == '\"')) {
                sbStandard.append('\'');
                if (!bDoubleQuoted)
                    bDoubleQuoted = true;
                else {
                    /* two double quotes represent a single one */
                    if ((i >= iLength - 1) || (sNativeSql.charAt(i + 1) != '\''))
                        bDoubleQuoted = false;
                    else {
                        sbStandard.append('\'');
                        i++;
                    }
                }
            } else if ((!bIdQuoted) && (!bSingleQuoted) && (c == '\'')) {
                /* double the single quote */
                sbStandard.append(c);
                sbStandard.append(c);
            } else
                sbStandard.append(c);
        }
        return sbStandard.toString();
    }


    /** convert a standard (ISO) SQL query to native (Jackcess) SQL
     * replacing all quoted identifiers by bracketed identifiers
     * replacing doubled double quotes by single ones.
     * @param sStandardSql standard SQL query.
     * @return native SQL.
     */
    public static String nativeSQL(String sStandardSql) {
        /* just replaces all double-quote delimited identifiers by bracketed ones */
        StringBuilder sbNativeSql = new StringBuilder();
        boolean bSingleQuoted = false;
        boolean bIdQuoted = false;
        for (int i = 0; i < sStandardSql.length(); i++) {
            char c = sStandardSql.charAt(i);
            if ((!bSingleQuoted) && (c == '"')) {
                if (!bIdQuoted) {
                    bIdQuoted = true;
                    sbNativeSql.append('[');
                } else {
                    /* two double-quotes need to become a single one belonging to the identifier */
                    if ((i >= sStandardSql.length() - 1) || (sStandardSql.charAt(i + 1) != '"')) {
                        sbNativeSql.append(']');
                        bIdQuoted = false;
                    } else {
                        sbNativeSql.append(c);
                        i++;
                    }
                }
            } else if (bIdQuoted && (c == ']')) {
                /* a closing brackets belonging to the identifier needs to be duplicated */
                sbNativeSql.append(c);
                sbNativeSql.append(c);
            } else if ((!bIdQuoted) && (c == '\'')) {
                sbNativeSql.append(c);
                if (!bSingleQuoted)
                    bSingleQuoted = true;
                else {
                    /* two single quotes represent a single one */
                    if ((i >= sStandardSql.length() - 1) || (sStandardSql.charAt(i + 1) != '\''))
                        bSingleQuoted = false;
                    else {
                        sbNativeSql.append(c);
                        i++;
                    }
                }
            } else
                sbNativeSql.append(c);
        }
        return sbNativeSql.toString();
    }

}
