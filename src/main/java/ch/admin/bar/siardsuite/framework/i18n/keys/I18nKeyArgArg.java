package ch.admin.bar.siardsuite.framework.i18n.keys;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class I18nKeyArgArg<TArg1, TArg2> implements Key {
    @NonNull String value;
}
