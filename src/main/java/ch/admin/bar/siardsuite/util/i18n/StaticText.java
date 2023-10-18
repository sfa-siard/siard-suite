package ch.admin.bar.siardsuite.util.i18n;

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
