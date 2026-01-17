/*====================================================================== 
Threads implements utilities for detecting the main thread/class. 
Application : JavaFX Utilities
Description: Threads implements utilities for detecting the main thread/class. 
Platform   : JAVA 1.7, JavaFX 2.2
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2017
Created    : 23.06.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.enterag.utils.lang;

import ch.enterag.utils.*;
import ch.enterag.utils.logging.*;

/*====================================================================*/
/** Threads implements utilities for detecting the main thread/class.
 * @author Hartwig Thomas
 */
public abstract class Threads
{
  private static IndentLogger _il = IndentLogger.getIndentLogger(Threads.class.getName());

  /*------------------------------------------------------------------*/
  /** get the main thread as the thread with the smallest ID.
   * @return main thread.
   */
  public static Thread getMainThread()
  {
    _il.enter();
    Thread threadMain = null;
    int iActive = Thread.activeCount();
    Thread[] athread = new Thread[2*iActive];
    int iEnumerated = Thread.enumerate(athread);
    if (iEnumerated < athread.length)
    {
      long lMinId = Long.MAX_VALUE;
      for (int i = 0; i < iEnumerated; i++)
      {
        Thread thread = athread[i];
        if (thread.getId() < lMinId)
        {
          threadMain = thread;
          lMinId = thread.getId();
          // System.out.println(threadMain.getName()+": "+getMainClass(threadMain).getName());
        }
      }
    }
    _il.exit(threadMain.getName());
    return threadMain;
  } /* getMainThread */

  /*------------------------------------------------------------------*/
  /** return the class with which the application (JVM) was started.
   * More precisely: the main class in the given thread, which is
   * not a system main class as in JavaFX.
   * @param thread thread.
   * @return the main class in the given thread,
   */
  public static Class<?> getMainClass(Thread thread)
  {
    Class<?> clsMain = null;
    StackTraceElement[] aste = thread.getStackTrace();
    for (int i = 0; (clsMain == null) && (i < aste.length); i++)
    {
      String sClassName = aste[i].getClassName();
      String sMethodName = aste[i].getMethodName();
      // System.out.println("  "+sClassName+": "+sMethodName);
      if ((!sClassName.startsWith("java")) &&
          (!sClassName.startsWith("com.sun")) &&
          (!sClassName.startsWith("sun")) &&
          sMethodName.equals("main"))
      {
        try { clsMain = Class.forName(sClassName); }
        catch(ClassNotFoundException cnfe) { System.err.println(EU.getExceptionMessage(cnfe)); }
      }
    }
    return clsMain;
  } /* getMainClass */

} /* Threads */
