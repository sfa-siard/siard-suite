/*== IndentLogger.java =================================================
IndentLogger implements a customized logger with indentation. 
Version     : $Id: IndentLogger.java 461 2015-12-18 09:47:55Z hartwig $
Application : Logging Utilities
Description : IndentLogger implements a customized logger with indentation.
------------------------------------------------------------------------
Copyright  : 2010, 2012, 2016 Enter AG, Rüti ZH, Switzerland
Created    : 15.04.2010, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.logging;

import java.sql.*;
import java.util.*;
import java.util.logging.*;
import ch.enterag.utils.*;

/*====================================================================*/
/** IndentLogger extends java.util.logging.Logger with automatic
 * indentation, automatic method name detection on entry and exit.
 * @author Hartwig Thomas
 */
public class IndentLogger extends Logger
{
  /*=====================================================================
  Constants
  /*===================================================================*/
  /** amount to increase/decrease indentation */
  private static final int iINDENT_AMOUNT = 2;
  /** tag for logged "events" */
  private static final String sTAG_EVENT = "-- ";
  /** tag for logged method entry */
  private static final String sTAG_ENTER = ">> ";
  /** tag for logged method exit */
  private static final String sTAG_EXIT = "<< ";

  /*=====================================================================
  Properties
  /*===================================================================*/
  /*-------------------------------------------------------------------*/
  /** static indent property.
   */
  private static StringBuilder m_sbIndent = new StringBuilder();
   /** returns current indent amount.
  @return indent amount.
  */
  public int getIndent() { return m_sbIndent.toString().length(); } /* getIndent */
  /** sets indent amount.
  @param iIndent indent amount.
  */
  public void setIndent(int iIndent)
  { 
    int iPreviousIndent = getIndent();
    if (iIndent < 0)
      iIndent = 0;
    m_sbIndent.setLength(iIndent);
    for (int i = iPreviousIndent; i < iIndent; i++)
      m_sbIndent.setCharAt(i, ' ');
  } /* setIndent */
  
  /*====================================================================
  constructors
  ====================================================================*/
  /*-------------------------------------------------------------------*/
  /** constructor (see java.util.logging.Logger).
   * Protected method to construct a logger for a named subsystem.
   * The logger will be initially configured with a null Level and with 
   * useParentHandlers true.
   * @param sName a name for the logger. This should be a dot-separated 
   *              name and should normally be based on the package name 
   *              or class name of the subsystem, such as java.net or 
   *              javax.swing. It may be null for anonymous Loggers.
   * @param sResources name of ResourcePropertyBundle to be used for localizing 
   *              messages for this logger. May be null if none of 
   *              the messages require localization.
   * @throws MissingResourceException if the ResourceBundleName is non-null
   *              and no corresponding resource can be found. 
   */
  protected IndentLogger(String sName, String sResources)
    throws MissingResourceException
  {
    super(sName, sResources);
  } /* constructor IndentLogger */
  
  /*====================================================================
  methods
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** returns the full name of the calling method which has given depth 
   * on stack. 
   * @param iDepth depth on stack.
   * @return full name of the calling method.
  */
  private String getCallingMethod(int iDepth)
  {
    String sCallingMethod = null;
    
    StackTraceElement[] asSte = Thread.currentThread().getStackTrace();
    String sClassName = asSte[iDepth].getClassName();
    String sMethodName = asSte[iDepth].getMethodName();
    sCallingMethod = sClassName + "." + sMethodName;
    return sCallingMethod;
  } /* getCallingMethod */
  
  /*====================================================================
  Overrides
  /*==================================================================*/
  /*------------------------------------------------------------------*/
  /** every log action passes through this.
   * @param record log record.
  */
  @Override
  public void  log(LogRecord record)
  {
    if (isLoggable(record.getLevel()))
      super.log(record);
  } /* log */
  
  /*====================================================================
  Indented logging
  /*==================================================================*/
  /*------------------------------------------------------------------*/
  /** logs an indented message object with the FINER level. 
  @param sMessage the message to log.
  */
  public synchronized void event(String sMessage)
  {
    finer(m_sbIndent.toString()+sTAG_EVENT+sMessage);
  } /* event */
  
  /*------------------------------------------------------------------*/
  /** logs an indented method and its parameters with the FINEST level 
   * and increases indentation.
   * @param aoParm method parameter values to be logged.
  */
  public synchronized void enter(Object... aoParm)
  {
    if (isLoggable(Level.FINEST))
    {
      StringBuilder sbMethod = new StringBuilder(getCallingMethod(3));
      sbMethod.append("(");
      for (int iParameter = 0; iParameter < aoParm.length; iParameter++)
      {
        if (iParameter > 0)
          sbMethod.append(", ");
        sbMethod.append(String.valueOf(aoParm[iParameter]));
      }
      sbMethod.append(")");
      finest(m_sbIndent.toString()+sTAG_ENTER+sbMethod.toString());
      setIndent(getIndent()+iINDENT_AMOUNT);
    }
  } /* enter */
  
  /*------------------------------------------------------------------*/
  /** decreases indentation and logs an indented method and its return
   * value with the FINEST level.
  */
  public synchronized void exit()
  {
    if (isLoggable(Level.FINEST))
    {
      StringBuilder sbMethod = new StringBuilder(getCallingMethod(3));
      setIndent(getIndent()-iINDENT_AMOUNT);
      finest(m_sbIndent.toString()+sTAG_EXIT+sbMethod.toString());
    }
  } /* exit */
  
  /*------------------------------------------------------------------*/
  /** decreases indentation and logs an indented method and its return
   * value with the FINEST level.
   @param oReturn return value to be logged.
  */
  public synchronized void exit(Object oReturn)
  {
    if (isLoggable(Level.FINEST))
    {
      StringBuilder sbMethod = new StringBuilder(getCallingMethod(3));
      setIndent(getIndent()-iINDENT_AMOUNT);
      finest(m_sbIndent.toString()+sTAG_EXIT+sbMethod.toString()+"("+String.valueOf(oReturn)+")");
    }
  } /* exit */
  
  /*------------------------------------------------------------------*/
  /** logs the given properties.
   @param sTitle properties' title in log.
   @param prop properties to be logged.
  */
  public final void properties(
      String sTitle, 
      Properties prop)
  {
    event(sTitle+":");
    setIndent(getIndent()+iINDENT_AMOUNT);
    String sPropKey = null;
    String sPropValue = null;
    for (Enumeration<?> enumProperty = prop.propertyNames();
         enumProperty.hasMoreElements();
        )
    {
      sPropKey = (String)enumProperty.nextElement();
      sPropValue = prop.getProperty(sPropKey);
      info("  "+sPropKey + ": " + sPropValue);
    }
    setIndent(getIndent()-iINDENT_AMOUNT);
  } /* logProperties */
  
  /*------------------------------------------------------------------*/
  /** logs the current system properties.
  */
  public final void systemProperties()
  {
    Runtime rt = Runtime.getRuntime();
    info("free memory: "+String.valueOf(rt.freeMemory()));
    info("total memory: "+String.valueOf(rt.totalMemory()));
    info("maximum memory: "+String.valueOf(rt.maxMemory()));
    Properties propSystem = System.getProperties();
    properties("System properties", propSystem);
  } /* systemProperties */
  
  /*------------------------------------------------------------------*/
  /** logs an error with the INFO level. 
  @param e error to log.
  */
  public synchronized void error(Error e)
  {
    if (isLoggable(Level.INFO))
      event(EU.getErrorMessage(e));
  } /* error */

  /*------------------------------------------------------------------*/
  /** logs an exception with the FINER level. 
  @param e exception to log.
  */
  public synchronized void exception(Exception e)
  {
    if (isLoggable(Level.FINER))
      event(EU.getExceptionMessage(e));
  } /* exception */

  /*------------------------------------------------------------------*/
  /** logs an SQLWarning with the FINER level. 
  @param sw warning to log.
  */
  public synchronized void sqlwarning(SQLWarning sw)
  {
    if (isLoggable(Level.FINER))
    {
      StringBuilder sbMessage = null;
      for (; sw != null; sw = sw.getNextWarning())
      {
        if (sbMessage == null)
          sbMessage = new StringBuilder();
        else
          sbMessage = sbMessage.append("\n");
        sbMessage.append("Warning "+sw.getMessage()+" / SQL State "+sw.getSQLState()+" / Error Code "+sw.getErrorCode());
      }
      if (sbMessage != null)
        event(sbMessage.toString());
    }
  } /* sqlwarning */

  /*====================================================================
  factories
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** returns a new IndentLogger, attempting to initialize it from the
   * logging configuration or from the system property first.
   * @param sName name of the logger.
   * @return the logger
  */
  public static IndentLogger getIndentLogger(String sName)
  {
    IndentLogger il = new IndentLogger(sName, null);
    LogManager lm = LogManager.getLogManager();
    lm.addLogger(il); // sets parent, inherits handlers, level is null, if parent's level is to be used.
    return il;
  } /* getIndentLogger */
  
} /* class IndentLogger */
