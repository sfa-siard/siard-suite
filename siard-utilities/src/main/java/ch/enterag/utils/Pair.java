/*======================================================================
Pair implements an ordered pair of objects.
Application : Utilities
Description : Pair implements an ordered pair of objects.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2008
Created    : 18.07.2017, Hartwig Thomas
======================================================================*/
package ch.enterag.utils;

import java.util.Objects;

/*====================================================================*/
/** Pair implements an ordered pair of objects.
 * Can be used for Triplets (Pair&lt;Cl1,Pair&lt;Cl2,Cl3&gt;&gt;) etc.
 * @author Hartwig Thomas
 * @param <CLFIRST> type of first component.
 * @param <CLSECOND> type of second component.
 */
public class Pair<CLFIRST,CLSECOND>
{
  private CLFIRST _f = null;
  public CLFIRST getFirst() { return _f; }
  private CLSECOND _s = null;
  public CLSECOND getSecond() { return _s; }
  
  /*------------------------------------------------------------------*/
  /** Constructor
   * @param f first component.
   * @param s second component.
   */
  public Pair(CLFIRST f, CLSECOND s)
  {
    _f = f;
    _s = s;
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** hashCode identifies the pair by its components and meshes with equals.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(_f,_s);
  } /* hashCode */

  /*------------------------------------------------------------------*/
  /** equality of two objects including null-equality.
   * @param o1 first object.
   * @param o2 second object.
   * @return true if they are equal or both null.
   */
  public static boolean areEqual(Object o1, Object o2)
  {
    boolean bEqual = false;
    if (o1 != null)
    {
      if (o1.equals(o2))
        bEqual = true;
    }
    else if (o2 == null)
      bEqual = true;
    return bEqual;
  } /* areEqual */
  
  /*------------------------------------------------------------------*/
  /** symmetric static comparison for equality.
   * Two pairs are equal, if their components are equal.
   * (We include "equality" of null values.)
   * @param p1 first pair.
   * @param p2 second pair.
   * @return true, if they are equal.
   */
  public static boolean areEqual(Pair<?,?> p1, Pair<?,?> p2)
  {
    boolean bEqual = false;
    if (p1 != null)
    {
      if (p2 != null)
        bEqual = areEqual(p1._f,p2._f) && areEqual(p1._s,p2._s);
    }
    else if (p2 == null)
      bEqual = true;
    return bEqual;
  }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   */
  @Override
  public boolean equals(Object o)
  {
    boolean bEqual = false;
    if ((o != null) && (o instanceof Pair))
    {
      Pair<?,?> pair = (Pair<?,?>)o;
      bEqual = areEqual(this,pair);
    }
    return bEqual;
  } /* equals */
} /* Pair */
