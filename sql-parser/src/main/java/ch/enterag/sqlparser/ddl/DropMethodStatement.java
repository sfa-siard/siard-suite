package ch.enterag.sqlparser.ddl;

import ch.enterag.sqlparser.K;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.antlr4.EnhancedSqlBaseVisitor;
import ch.enterag.sqlparser.antlr4.SqlBase;
import ch.enterag.sqlparser.ddl.enums.DropBehavior;
import ch.enterag.sqlparser.generated.SqlParser;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.logging.IndentLogger;

import java.util.ArrayList;
import java.util.List;

public class DropMethodStatement
        extends SqlBase {
    /** logger */
    private static IndentLogger _il = IndentLogger.getIndentLogger(DropMethodStatement.class.getName());


    /** visitor initializes fields from parse tree.
     */
    private class DmsVisitor extends EnhancedSqlBaseVisitor<DropMethodStatement> {
        @Override
        public DropMethodStatement visitDropMethodStatement(SqlParser.DropMethodStatementContext ctx) {
            if (ctx.SPECIFIC() != null)
                setSpecific(true);
            if ((ctx.LEFT_PAREN() != null) && (ctx.RIGHT_PAREN() != null))
                setParameterList(true);
            setRoutineName(ctx.routineName(), getMethodName());
            return visitChildren(ctx);
        }

        @Override
        public DropMethodStatement visitSqlParameterDeclaration(SqlParser.SqlParameterDeclarationContext ctx) {
            SqlParameterDeclaration spd = getSqlFactory().newSqlParameterDeclaration();
            spd.parse(ctx);
            getParameters().add(spd);
            return DropMethodStatement.this;
        }

        @Override
        public DropMethodStatement visitUdtName(SqlParser.UdtNameContext ctx) {
            setUdtName(ctx, getUdtName());
            return DropMethodStatement.this;
        }

        @Override
        public DropMethodStatement visitDropBehavior(SqlParser.DropBehaviorContext ctx) {
            setDropBehavior(getDropBehavior(ctx));
            return DropMethodStatement.this;
        }
    }

    private DmsVisitor _visitor = new DmsVisitor();

    private DmsVisitor getVisitor() {
        return _visitor;
    }

    private boolean _bSpecific = false;

    public boolean isSpecific() {
        return _bSpecific;
    }

    public void setSpecific(boolean bSpecific) {
        _bSpecific = bSpecific;
    }

    private QualifiedId _qiMethodName = new QualifiedId();

    public QualifiedId getMethodName() {
        return _qiMethodName;
    }

    private void setMethodName(QualifiedId qiMethodName) {
        _qiMethodName = qiMethodName;
    }

    private boolean _bParameterList = false;

    public boolean hasParameterList() {
        return _bParameterList;
    }

    public void setParameterList(boolean bParameterList) {
        _bParameterList = bParameterList;
    }

    private List<SqlParameterDeclaration> _listParameters = new ArrayList<SqlParameterDeclaration>();

    public List<SqlParameterDeclaration> getParameters() {
        return _listParameters;
    }

    private void setParameters(List<SqlParameterDeclaration> listParameters) {
        _listParameters = listParameters;
    }

    private QualifiedId _qiUdtName = new QualifiedId();

    public QualifiedId getUdtName() {
        return _qiUdtName;
    }

    private void setUdtName(QualifiedId qiUdtName) {
        _qiUdtName = qiUdtName;
    }

    private DropBehavior _db = null;

    public DropBehavior getDropBehavior() {
        return _db;
    }

    public void setDropBehavior(DropBehavior db) {
        _db = db;
    }

    private String formatParameters() {
        String s = sLEFT_PAREN;
        for (int i = 0; i < getParameters().size(); i++) {
            if (i > 0)
                s = s + sCOMMA + sSP;
            s = s + getParameters().get(i)
                                   .format();
        }
        s = s + sRIGHT_PAREN;
        return s;
    }


    /** format the drop method statement.
     * @return the SQL string corresponding to the fields of the drop method statement.
     */
    @Override
    public String format() {
        String sStatement = K.DROP.getKeyword();
        if (isSpecific())
            sStatement = sStatement + sSP + K.SPECIFIC.getKeyword();
        sStatement = sStatement + sSP + K.METHOD.getKeyword() + sSP +
                getMethodName().format();
        if (hasParameterList())
            sStatement = sStatement + formatParameters();
        sStatement = sStatement + sSP + K.FOR.getKeyword() + sSP +
                getUdtName().format() + sSP + getDropBehavior().getKeywords();
        return sStatement;
    }


    /** parse the drop method statement from the parsing tree context.
     * @param ctx parsing context (tree).
     * @throws NullPointerException if no parsing tree is available.
     */
    public void parse(SqlParser.DropMethodStatementContext ctx) {
        setContext(ctx);
        getVisitor().visit(getContext());
    }


    /** parse the drop method statement from SQL.
     * @param sSql SQL.
     */
    @Override
    public void parse(String sSql) {
        setParser(newSqlParser(sSql));
        parse(getParser().dropMethodStatement());
    }


    /** initialize drop method statement.
     * @param bSpecific true for SPECIFIC.
     * @param qiFunctionName name of function to be dropped (not null).
     * @param bParameterList true, if parameter declarations identify function.
     * @param listParameters parameter declarations (not null!)
     * @param qiUdtName name of UDT associated with method.
     * @param db drop behavior (not null!).
     */
    public void initialize(
            boolean bSpecific,
            QualifiedId qiFunctionName,
            boolean bParameterList,
            List<SqlParameterDeclaration> listParameters,
            QualifiedId qiUdtName,
            DropBehavior db
    ) {
        _il.enter();
        setSpecific(bSpecific);
        setMethodName(qiFunctionName);
        setParameterList(bParameterList);
        setParameters(listParameters);
        setUdtName(qiUdtName);
        setDropBehavior(db);
        _il.exit();
    }


    /** constructor with factory only to be called by factory.
     * @param sf factory.
     */
    public DropMethodStatement(SqlFactory sf) {
        super(sf);
    }

}
