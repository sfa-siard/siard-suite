/*== EU.java ===========================================================
Exception utilities.
Application : Utilities
Description : XML utilities. 
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2016
Created    : 28.08.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.enterag.utils;

public abstract class EU
{
  /*------------------------------------------------------------------*/
  /* constructs a full message with all causes from a throwable.
   * @return full message.
   */
  public static String getThrowableMessage(Throwable t)
  {
    String sMessage = "";
    Throwable tException = t;
    for (; tException != null; tException = tException.getCause())
    {
      if (tException == t)
        sMessage = sMessage + tException.getClass().getName() + ": ";
      else
        sMessage = sMessage + "< " + tException.getClass().getName()+": ";
      if (tException.getMessage() != null)
        sMessage = sMessage + tException.getMessage();
    }
    return "  "+sMessage;
  } /* getThrowableMessage */
  
  /*------------------------------------------------------------------*/
  /** retrieves a full error message. 
   * @param e error.
   * @return full message.
   */
  public static String getErrorMessage(Error e)
  {
    return getThrowableMessage(e);
  } /* getErrorMessage */

  /*------------------------------------------------------------------*/
  /** retrieves a full exception message. 
   * @param e exception.
   * @return full message.
   */
  public static String getExceptionMessage(Exception e)
  {
    return getThrowableMessage(e);
  } /* getExceptionMessage */

} /* class EU */
