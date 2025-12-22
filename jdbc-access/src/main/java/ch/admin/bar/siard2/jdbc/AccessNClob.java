/*======================================================================
AccessNClob implements an NClob as a trivial string-based Clob.
Version     : $Id: $
Application : SIARD2
Description : AccessNClob implements an NClob as a trivial string-based Clob.
              See https://docs.oracle.com/javase/7/docs/api/java/sql/NClob.html
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2017, Enter AG, Rüti ZH, Switzerland
Created    : 07.03.2017, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.NClob;


/** AccessNClob implements an NClob as a trivial string-based Clob.
 * @author Hartwig Thomas
 */
public class AccessNClob extends AccessClob implements NClob {
}
