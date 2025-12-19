package ch.admin.bar.siardsuite.framework.i18n.keys;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class I18nKey implements Key {
    @NonNull String value;
}
