package ch.admin.bar.siardsuite.util;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class I18nKey {
    @NonNull String value;
}
