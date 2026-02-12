package ch.enterag.sqlparser.antlr4;

import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.generated.*;

public abstract class SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SqlBase.class.getName());
  /*==================================================================*/
  /** visitor lists the parse tree.
   */
  private class BaseVisitor extends SqlBaseVisitor<SqlBase>
  {
    private static final String sINDENT = "  ";
    private String _sIndent = "";
    @Override 
    public SqlBase visitChildren(RuleNode ctx)
    {
      _sIndent = _sIndent + sINDENT;
      // System.out.println(_vocabulary.getSymbolicName(ctx.getRuleContext().getRuleIndex()));
      SqlBase sbReturn = null;
      for (int i = 0; i < ctx.getChildCount(); i++)
      {
        ParseTree pt = ctx.getChild(i);
        if (pt instanceof RuleNode)
        {
          RuleNode ctxChild = (RuleNode)pt;
          sbReturn = visitChildren(ctxChild);
        }
        else if (pt instanceof TerminalNode)
        {
          TerminalNode tn = (TerminalNode)pt;
          sbReturn = visitTerminal(tn);
        }
        else if (pt instanceof ErrorNode)
        {
          ErrorNode en = (ErrorNode)pt;
          sbReturn = visitErrorNode(en);
        }
      }
      _sIndent = _sIndent.substring(0,_sIndent.length()-sINDENT.length());
      return sbReturn;
    }
    @Override
    public SqlBase visitErrorNode(ErrorNode node)
    {
      System.out.println("ERROR: "+node.toString());
      return SqlBase.this;
    }
    @Override 
    public SqlBase visitTerminal(TerminalNode node)
    {
      Token token = node.getSymbol();
      int iType = token.getType();
      String sName = SqlParser.VOCABULARY.getSymbolicName(iType); 
      if (sName == null)
        sName = SqlParser.VOCABULARY.getLiteralName(iType);
      if (sName == null)
        sName = SqlParser.VOCABULARY.getDisplayName(iType);
      System.out.println(_sIndent + token.getText()+" "+sName);
      return SqlBase.this;
    }
  }
  /*==================================================================*/
  
  public static final String sIDENTIFIER = "IDENTIFIER"; /* identifier symbolic name */
  public static final String sNEW_LINE = "\r\n";
  public static final String sINDENT = "  ";
  public static final String sSP = " ";
  public static final String sPERIOD = ".";
  public static final String sCOMMA = ",";
  public static final String sLEFT_PAREN = "(";
  public static final String sRIGHT_PAREN = ")";
  public static final String sLEFT_BRACKET = "[";
  public static final String sRIGHT_BRACKET = "]";
  public static final String sCONCATENATION_OPERATOR = "||";
  public static final String sQUESTION_MARK = "?";
  public static final String sDOUBLE_COLON = "::";
  public static final String sDEREFERENCE_OPERATOR = "->";
  public static final String sASTERISK = "*";
  public static final String sEQUALS = "=";

  private static boolean _bDebug = false;
  public static boolean isDebug() { return _bDebug; }
  public static void setDebug(boolean bDebug) { _bDebug = bDebug; }
  
  /** factory */
  private SqlFactory _sf = null;
  protected SqlFactory getSqlFactory() { return _sf; }
  private void setSqlFactory(SqlFactory sf) { _sf = sf; }

  /** parser */
  private SqlParser _parser = null;
  protected SqlParser getParser() { return _parser; }
  public void setParser(SqlParser parser) { _parser = parser; }
  /** parsing context of the object */
  private ParserRuleContext _ctx = null;
  protected ParserRuleContext getContext() { return _ctx; }
  public void setContext(ParserRuleContext ctx) { _ctx = ctx; }

  /* N.B.: unchecked exception! */
  public void throwParseCancellationException(String sMessage)
  {
    throw new ParseCancellationException(sMessage);
  }
  
  private static void listTokens(CommonTokenStream cts)
  {
    for (int i = 0; i < cts.getNumberOfOnChannelTokens(); i++)
    {
      Token token = cts.get(i);
      System.out.println(String.valueOf(i)+": "+token.getText()+" "+SqlParser.VOCABULARY.getSymbolicName(token.getType()));
    }
  }
  /** list the result of the lexer rules.
   * N.B. lexer rules are tricky - a lot depends on the order of the rules!
   */
  public void listTokens()
  {
    CommonTokenStream cts = (CommonTokenStream)getParser().getTokenStream();
    listTokens(cts);
  }
  
  public void listTree(ParserRuleContext ctx)
  {
    BaseVisitor bv = new BaseVisitor();
    bv.visit(ctx);
  }
  
  public static SqlParser newSqlParser(String sSql)
  {
    SqlParser sp = null;
    try
    {
      ByteArrayInputStream bis = new ByteArrayInputStream(sSql.getBytes());
      ANTLRInputStream ais = new ANTLRInputStream(bis);
      SqlLexer sqlLexer = new SqlLexer(ais);
      CommonTokenStream cts = new CommonTokenStream(sqlLexer);
      if (isDebug())
        listTokens(cts);
      sp = new SqlParser(cts);
      sp.getErrorListeners().clear();
      sp.addErrorListener(ErrorListener.getInstance());
      sp.getInterpreter().setPredictionMode(PredictionMode.SLL);
    }
    catch(IOException ie) { _il.exception(ie); }
    return sp;
  } /* newSqlParser */
  
  public static SqlParser newSqlParser2(String sSql)
  {
    SqlParser sp = null;
    try
    {
      ByteArrayInputStream bis = new ByteArrayInputStream(sSql.getBytes());
      ANTLRInputStream ais = new ANTLRInputStream(bis);
      SqlLexer sqlLexer = new SqlLexer(ais);
      CommonTokenStream cts = new CommonTokenStream(sqlLexer);
      if (isDebug())
        listTokens(cts);
      sp = new SqlParser(cts);
      sp.getErrorListeners().clear();
      sp.addErrorListener(ErrorListener.getInstance());
      sp.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
    }
    catch(IOException ie) { _il.exception(ie); }
    return sp;
  } /* newSqlParser */
  
  /* every part of the SQL parser must be created by a factory */
  public SqlBase(SqlFactory sf)
  {
    setSqlFactory(sf);
  } /* constructor Base */
  
  /* every parsable object can be formated */
  public abstract String format();
  
  /* create the parser for SQL */
  public abstract void parse(String sSql);

} /* SqlBase */
