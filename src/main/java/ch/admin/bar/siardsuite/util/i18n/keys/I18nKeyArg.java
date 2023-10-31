package ch.admin.bar.siardsuite.util.i18n.keys;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class I18nKeyArg<TArg> implements Key {
    @NonNull String value;
}
