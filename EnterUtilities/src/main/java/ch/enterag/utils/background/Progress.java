/*======================================================================
Minimum interface for long-running methods.
Application : SIARD 2.0
Description : Minimum two-way notification interface for long-running 
              methods.
              This interface decouples the long-running method
              from the implementation of the thread-handling.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 15.10.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.enterag.utils.background;

public interface Progress {
    /*------------------------------------------------------------------*/

    /**
     * check, if cancel was requested.
     * (To be called by long-running method.)
     *
     * @return true, if cancel was requested.
     */
    public boolean cancelRequested();

    /*------------------------------------------------------------------*/

    /**
     * notify progress in percent.
     * (To be called by long-running method.)
     * N.B.: Ignored when cancel requested.
     *
     * @param iPercent percentage of work done.
     */
    public void notifyProgress(int iPercent);

} /* Progress */
