package ch.enterag.sqlparser.expression.enums;

import ch.enterag.sqlparser.K;

public enum OrderingSpecification {
    ASC(K.ASC.getKeyword()),
    DESC(K.DESC.getKeyword());
    private String _sKeywords = null;

    public String getKeywords() {
        return _sKeywords;
    }

    private OrderingSpecification(String sKeywords) {
        _sKeywords = sKeywords;
    }
}
