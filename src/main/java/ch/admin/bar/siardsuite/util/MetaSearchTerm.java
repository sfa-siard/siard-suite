package ch.admin.bar.siardsuite.util;

import java.util.Map;
import java.util.regex.Pattern;

public class MetaSearchTerm {
    private final String term;

    public MetaSearchTerm(String term) {
        this.term = term;
    }

    boolean matches(String value) {
        return term != null
                && value != null
                && !term.isEmpty()
                && !value.isEmpty()
                && Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE).matcher(value).find();
    }

    boolean matches(Map map) {
        for (Object key : map.keySet()) {
            if (map.get(key) instanceof String && this.matches((String) map.get(key))) {
                return true;
            }
        }
        return false;
    }
}
