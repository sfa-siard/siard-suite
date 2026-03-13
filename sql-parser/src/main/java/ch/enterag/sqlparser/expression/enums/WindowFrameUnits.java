package ch.enterag.sqlparser.expression.enums;

import ch.enterag.sqlparser.K;

public enum WindowFrameUnits {
    ROWS(K.ROWS.getKeyword()),
    RANGE(K.RANGE.getKeyword());
    private String _sKeywords = null;

    public String getKeywords() {
        return _sKeywords;
    }

    private WindowFrameUnits(String sKeywords) {
        _sKeywords = sKeywords;
    }
}
