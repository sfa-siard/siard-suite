package ch.enterag.sqlparser.antlr4;

import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.*;

import ch.enterag.utils.logging.IndentLogger;

public class ErrorListener
  implements ANTLRErrorListener
{
  /** singleton instance */
  private static ErrorListener _en = null;
  
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(ErrorListener.class.getName());
  
  private boolean bExceptionSuppressed = true;
  private boolean isExceptionSuppressed() { return bExceptionSuppressed; }
  // public void suppressException() { bExceptionSuppressed = true; }
  // public void resetExceptionSuppressed() { bExceptionSuppressed = false; }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void reportAmbiguity(Parser recognizer, DFA dfa, 
    int startIndex, int stopIndex,
    boolean exact, BitSet ambigAlts, ATNConfigSet configs)
  {
    _il.enter(recognizer, dfa, 
    String.valueOf(startIndex), String.valueOf(stopIndex),
    String.valueOf(exact), ambigAlts, configs);
    String sInput = "";
    for (int i = startIndex; i <= stopIndex; i++)
      sInput = sInput + recognizer.getTokenStream().get(i).getText();
    if (ambigAlts == null)
      ambigAlts = configs.getAlts();
    _il.exit();
    if (!isExceptionSuppressed())
      throw new IllegalArgumentException("SQL ambiguity for input \"" +sInput+"\" "+
        ambigAlts.toString());
  } /* reportAmbiguity */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void reportAttemptingFullContext(Parser recognizer, DFA dfa,
      int startIndex, int stopIndex, 
      BitSet conflictingAlts, ATNConfigSet configs)
  {
    _il.enter(recognizer, dfa,
    String.valueOf(startIndex), String.valueOf(stopIndex),
    conflictingAlts, configs);
    _il.exit();
    if (!isExceptionSuppressed())
      throw new IllegalArgumentException("SQL attempting full context for substring " +
        String.valueOf(startIndex)+" to "+String.valueOf(stopIndex));
  } /* reportAttemptingFullContext */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void reportContextSensitivity(Parser recognizer, DFA dfa,
      int startIndex, int stopIndex, int prediction, ATNConfigSet configs)
  {
    _il.enter(recognizer, dfa, 
    String.valueOf(startIndex), String.valueOf(stopIndex),
    String.valueOf(prediction), configs);
    _il.exit();
    if (!isExceptionSuppressed())
      throw new IllegalArgumentException("SQL context sensitivity for substring " +
        String.valueOf(startIndex)+" to "+String.valueOf(stopIndex));
  } /* reportContextSensitivity */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
      int line, int charPositionInLine, String msg, RecognitionException e)
  {
    _il.enter(recognizer, offendingSymbol, 
      String.valueOf(line), String.valueOf(charPositionInLine), msg, e);
    List<String> listRuleStack = ((Parser)recognizer).getRuleInvocationStack();
    Collections.reverse(listRuleStack);
    _il.exit();
    throw new IllegalArgumentException("SQL parsing error at position "
      +String.valueOf(line)+"/"+String.valueOf(charPositionInLine)+
      " at "+String.valueOf(offendingSymbol)+": "+msg+"\r\nRule stack: "+String.valueOf(listRuleStack));
  } /* syntaxError */

  /*------------------------------------------------------------------*/
  /** private constructor for singleton.
   */
  private ErrorListener()
  {
  } /* constructor ErrorListener */
  
  /*------------------------------------------------------------------*/
  /** singleton factory 
   * @return singleton error listener.
   */
  public static ErrorListener getInstance()
  {
    if (_en == null)
      _en = new ErrorListener();
    return _en;
  } /* getInstance */

} /* ErrorListener */
