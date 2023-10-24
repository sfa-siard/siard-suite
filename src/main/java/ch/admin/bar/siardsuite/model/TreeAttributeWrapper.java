package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class TreeAttributeWrapper {

    @NonNull DisplayableText name;
    @NonNull DisplayableText viewTitle;
    @NonNull RenderableForm<?> renderableForm;

    @Override
    public String toString() {
        return name.getText();
    }
}
