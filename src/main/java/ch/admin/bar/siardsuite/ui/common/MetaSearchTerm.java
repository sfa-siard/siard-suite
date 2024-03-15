package ch.admin.bar.siardsuite.ui.common;

import java.util.regex.Pattern;

public class MetaSearchTerm {
    private final String term;

    public MetaSearchTerm(String term) {
        this.term = term;
    }

    public boolean matches(String value) {
        return term != null
                && value != null
                && !term.isEmpty()
                && !value.isEmpty()
                && Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE).matcher(value).find();
    }
}
