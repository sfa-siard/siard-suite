package ch.admin.bar.siardsuite.framework.i18n;

import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.framework.i18n.keys.Key;
import lombok.Value;

@Value
public class TranslatableText implements DisplayableText {

    Key key;
    Object[] args;

    public static TranslatableText of(Key key, Object... args) {
        return new TranslatableText(key, args);
    }

    @Override
    public String getText() {
        return I18n.get(key, args);
    }
}
