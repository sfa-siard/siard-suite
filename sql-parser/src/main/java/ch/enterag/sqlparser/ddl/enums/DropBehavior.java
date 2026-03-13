package ch.enterag.sqlparser.ddl.enums;

import ch.enterag.sqlparser.K;

public enum DropBehavior {
    CASCADE(K.CASCADE.getKeyword()),
    RESTRICT(K.RESTRICT.getKeyword());
    private String _sKeywords = null;

    public String getKeywords() {
        return _sKeywords;
    }

    private DropBehavior(String sKeywords) {
        _sKeywords = sKeywords;
    }
}
