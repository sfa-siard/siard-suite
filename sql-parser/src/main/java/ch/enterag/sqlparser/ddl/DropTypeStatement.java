package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.antlr4.EnhancedSqlBaseVisitor;
import ch.enterag.sqlparser.antlr4.SqlBase;
import ch.enterag.sqlparser.ddl.enums.DropBehavior;
import ch.enterag.sqlparser.generated.SqlParser;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.logging.IndentLogger;

public class DropTypeStatement
        extends SqlBase {
    /** logger */
    private static IndentLogger _il = IndentLogger.getIndentLogger(DropTypeStatement.class.getName());

    /*==================================================================*/

    /** visitor initializes fields from parse tree.
     */
    private class DtsVisitor extends EnhancedSqlBaseVisitor<DropTypeStatement> {
        @Override
        public DropTypeStatement visitUdtName(SqlParser.UdtNameContext ctx) {
            setUdtName(ctx, getUdtName());
            return DropTypeStatement.this;
        }

        @Override
        public DropTypeStatement visitDropBehavior(SqlParser.DropBehaviorContext ctx) {
            setDropBehavior(getDropBehavior(ctx));
            return DropTypeStatement.this;
        }
    }
    /*==================================================================*/

    private DtsVisitor _visitor = new DtsVisitor();

    private DtsVisitor getVisitor() {
        return _visitor;
    }

    private QualifiedId _qUdtName = new QualifiedId();

    public QualifiedId getUdtName() {
        return _qUdtName;
    }

    private void setUdtName(QualifiedId qUdtName) {
        _qUdtName = qUdtName;
    }

    private DropBehavior _db = null;

    public DropBehavior getDropBehavior() {
        return _db;
    }

    public void setDropBehavior(DropBehavior db) {
        _db = db;
    }

    /*------------------------------------------------------------------*/

    /** format the drop type statement.
     * @return the SQL string corresponding to the fields of the drop type statement.
     */
    @Override
    public String format() {
        String sStatement = K.DROP.getKeyword() + sSP + K.TYPE.getKeyword() + sSP +
                getUdtName().format() + sSP + getDropBehavior().getKeywords();
        return sStatement;
    } /* format */

    /*------------------------------------------------------------------*/

    /** parse the drop type statement from the parsing tree context.
     * @param ctx parsing context (tree).
     * @throws NullPointerException if no parsing tree is available.
     */
    public void parse(SqlParser.DropTypeStatementContext ctx) {
        setContext(ctx);
        getVisitor().visit(getContext());
    } /* parse */

    /*------------------------------------------------------------------*/

    /** parse the drop type statement from SQL.
     * @param sSql SQL.
     */
    @Override
    public void parse(String sSql) {
        setParser(newSqlParser(sSql));
        parse(getParser().dropTypeStatement());
    } /* parse */

    /*------------------------------------------------------------------*/

    /** initialize a drop type statement.
     * @param qUdtName name of UDT to be dropped.
     * @param db drop behavior (not null!).
     */
    public void initialize(QualifiedId qUdtName, DropBehavior db) {
        _il.enter(qUdtName, db);
        setUdtName(qUdtName);
        setDropBehavior(db);
        _il.exit();
    } /* initialize */

    /*------------------------------------------------------------------*/

    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
    public DropTypeStatement(SqlFactory sf) {
        super(sf);
    } /* constructor */

} /* class DropTypeStatement */
