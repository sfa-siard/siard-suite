package ch.admin.bar.siardsuite.util.i18n;

import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKeyArg;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKeyArgArg;
import javafx.beans.value.ObservableValue;

public interface DisplayableText {
    String getText();

    default ObservableValue<String> bindable() {
        return I18n.bind(this);
    }

    static DisplayableText of(String text) {
        return StaticText.of(text);
    }

    static DisplayableText of(I18nKey key) {
        return TranslatableText.of(key);
    }

    static <TArg> DisplayableText of(I18nKeyArg<TArg> key, TArg arg) {
        return TranslatableText.of(key, arg);
    }

    static <TArg1, TArg2> DisplayableText of(I18nKeyArgArg<TArg1, TArg2> key, TArg1 arg1, TArg2 arg2) {
        return TranslatableText.of(key, arg1, arg2);
    }
}
