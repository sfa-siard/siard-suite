package ch.admin.bar.siardsuite.util.i18n;

import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.Value;

@Value
public class TranslatableText implements DisplayableText {

    I18nKey i18nKey;
    Object[] args;

    public static TranslatableText of(I18nKey i18nKey, Object... args) {
        return new TranslatableText(i18nKey, args);
    }

    @Override
    public String getText() {
        return I18n.get(i18nKey, args);
    }
}
