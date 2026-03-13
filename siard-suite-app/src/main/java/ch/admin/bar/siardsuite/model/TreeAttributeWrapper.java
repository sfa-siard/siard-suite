package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableForm;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class TreeAttributeWrapper {

    @NonNull
    DisplayableText name;
    @NonNull
    DisplayableText viewTitle;
    @NonNull
    RenderableForm<?> renderableForm;

    @Override
    public String toString() {
        return name.getText();
    }
}
