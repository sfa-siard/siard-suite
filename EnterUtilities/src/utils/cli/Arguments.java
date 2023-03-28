/*== Arguments.java ====================================================
Trivial command-line parser
Version     : $Id: Arguments.java 539 2016-02-17 14:11:58Z hartwig $
Application : CLI Utilities
Description : The command-line is parsed into options (introduced by "-"
              or "/" (Windows only) identified by name and unnamed 
              arguments identified by position.
              Named options may occur anywhere. If they have a value,
              it must be separated from the option name by "=" or ":". 
              Unnamed arguments may not start with a "-" or "/" (on Windows). 
Platform    : JAVA SE 1.5 or higher  
------------------------------------------------------------------------
Copyright  : 2009, Enter AG, Zurich, Switzerland 
Created    : May 13, 2009, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.cli;

import java.io.*;
import java.util.*;

/*===================================================================*/
/** This class parses the command line and makes the arguments
 * accessible as named options by name and unnamed arguments by
 * position.
 * In the name of simplicity this class does support switches that 
 * indicate a boolean value just by their presence.
 * @author Hartwig Thomas
 */
public class Arguments
{
  /** container of named options */
  private Map<String,String> m_mapOptions = null;
  /** get value of parsed option. 
   * @param sName name of option 
   * @return option value (null for missing option, "" for missing value) */
  public String getOption(String sName) { return m_mapOptions.get(sName); }
  /** container of unnamed arguments */
  private String[] m_asArgument = null;
  /** get value of unnamed argument. 
   * @param iPosition position of argument (0 based) 
   * @return argument value */
  public String getArgument(int iPosition) { return m_asArgument[iPosition]; }
  /** @return number of unnamed arguments. */
  public int getArguments() { return m_asArgument.length; }
  /** error string */
  private String m_sError = null;
  /** @return syntax error on command line. */
  public String getError() { return m_sError; }
  
  /*------------------------------------------------------------------*/
  /** constructor parses the command-line arguments
   * @param args command-line arguments.
   */
  private Arguments(String[] args)
  {
    List<String> listArgument = new ArrayList<String>();
    m_mapOptions = new HashMap<String,String>();
    int iArgument = 0;
    while (iArgument < args.length)
    {
      String sArgument = args[iArgument]; 
      if (sArgument.startsWith("-") || (!File.separator.equals("/") && sArgument.startsWith("/")))
      {
        /* named argument: name is terminated by special character */
        int iPosition = 1;
        for (;
          ((iPosition < sArgument.length() &&
           Character.isLetterOrDigit(sArgument.charAt(iPosition))));
          iPosition++) {}
        if (iPosition > 1)
        {
          String sName = sArgument.substring(1,iPosition);
          String sValue = "";
          if (iPosition < sArgument.length())
          {
            if ((sArgument.charAt(iPosition) == ':') || 
                (sArgument.charAt(iPosition) == '='))
              sValue = sArgument.substring(iPosition+1); /* skip one ":", "=" */
            else
              m_sError = "Option " + sName + " must be terminated by colon, equals or blank!";
          }
          m_mapOptions.put(sName, sValue);
        }
        else
          m_sError = "Empty option encountered!";
      }
      else
        listArgument.add(args[iArgument]);
      iArgument++;
    }
    m_asArgument = listArgument.toArray(new String[]{});
  } /* constructor Arguments */
  
  /*------------------------------------------------------------------*/
  /** factory parses the command-line arguments and returns the result.
   * @param args command-line arguments.
   * @return instance of Arguments.
   */
  public static Arguments newInstance(String[] args)
  {
    return new Arguments(args);
  } /* newInstance */

} /* class Arguments */
