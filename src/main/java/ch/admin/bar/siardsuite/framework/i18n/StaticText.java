package ch.admin.bar.siardsuite.framework.i18n;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class StaticText implements DisplayableText {

    @NonNull String staticText;

    @Override
    public String getText() {
        return staticText;
    }
}
