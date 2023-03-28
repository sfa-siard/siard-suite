/*== SqlTypes.java =====================================================
Maps values of java.sql.Types to their name. 
Version     : $Id: SqlTypes.java 35 2015-02-05 14:43:18Z hartwigthomas $
Application : Database Utilities
Description : Maps values of java.sql.Types to their name.
------------------------------------------------------------------------
Copyright  : 2014, Enter AG, Zurich, Switzerland
Created    : 12.12.2014, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.database;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/*====================================================================*/
/** Maps values of java.sql.Types to their name.
 * @author Hartwig
 */
public abstract class SqlTypes
{
  public static final String sUNKNOWN = "UNKNOWN";
  
  /** translation from (int) Types value to type name */
  private static Map<Integer,String> _mapTypeNames = null;
  
  /*------------------------------------------------------------------*/
  /** initialize the translation table */
  private static void initialize()
  {
    _mapTypeNames = new HashMap<Integer, String>();
    Field[] afield = Types.class.getDeclaredFields();
    for (int iField = 0; iField < afield.length; iField++)
    {
      try
      {
        Field field = afield[iField];
        Integer iType = Integer.valueOf(field.getInt(null));
        String sTypeName = afield[iField].getName();
        _mapTypeNames.put(iType, sTypeName);
      }
      catch(IllegalAccessException iae) {}
    }
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** translate a type to its name
   * @param iType java.sql.Types value.
   * @return name of the type.
   */
  public static String getTypeName(int iType)
  {
    if (_mapTypeNames == null)
      initialize();
    String sType = _mapTypeNames.get(Integer.valueOf(iType));
    if (sType == null)
      sType = sUNKNOWN;
    return sType;
  } /* getTypeName */
  
  /*------------------------------------------------------------------*/
  /* return all types.
   * @return a sorted list of all types.
   */
  public static List<Integer> getAllTypes()
  {
    if (_mapTypeNames == null)
      initialize();
    List<Integer> listTypes = new ArrayList<Integer>(_mapTypeNames.keySet());
    Collections.sort(listTypes);
    return listTypes;
  } /* getAllTypes */
  
} /* class SqlTypes */
