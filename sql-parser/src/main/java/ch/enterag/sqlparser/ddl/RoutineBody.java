package ch.enterag.sqlparser.ddl;

import java.text.*;
import java.util.*;
import org.antlr.v4.runtime.tree.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.utils.logging.*;

public class RoutineBody
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(RoutineBody.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class RbVisitor extends EnhancedSqlBaseVisitor<RoutineBody>
  {
    @Override
    public RoutineBody visitTerminal(TerminalNode tn)
    {
      int iType = tn.getSymbol().getType();
      String sLiteral = SqlParser.VOCABULARY.getLiteralName(iType);
      String sDisplay = SqlParser.VOCABULARY.getDisplayName(iType);
      if (sLiteral != null)
        sDisplay = sLiteral.substring(1,sLiteral.length()-1);
      else if (tn.getSymbol().getType() == SqlParser.IDENTIFIER)
      {
        try { sDisplay = SqlLiterals.formatId(SqlLiterals.parseId(tn.getText())); }
        catch (ParseException pe) { throw new IllegalArgumentException("Cannot parse identifier: "+pe.getMessage()+"!"); }
      }
      else if (sDisplay.endsWith("_LITERAL") || 
               sDisplay.equals("UNSIGNED_INTEGER"))
        sDisplay = tn.getText();
      getTokens().add(sDisplay);
      return RoutineBody.this;
    }
  }
  /*==================================================================*/

  private RbVisitor _visitor = new RbVisitor();
  private RbVisitor getVisitor() { return _visitor; }

  private List<String> _listTokens = new ArrayList<String>();
  public List<String> getTokens() { return _listTokens; }
  private void setTokens(List<String> listTokens) { _listTokens = listTokens; }
  
  /*------------------------------------------------------------------*/
  /** format the routine body.
   * @return the SQL string corresponding to the fields of the routine body.
   */
  @Override
  public String format()
  {
    String s = "";
    for (int i = 0; i < getTokens().size();i++)
    {
      if (i > 0)
        s = s + sSP;
      s = s + getTokens().get(i);
    }
    return s;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the routine body from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.RoutineBodyContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the routine body from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().routineBody());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a routine body.
   * @param listTokens tokenized routine body.
   */
  public void initialize(List<String> listTokens)
  {
    _il.enter();
    setTokens(listTokens);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public RoutineBody(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class RoutineBody */
