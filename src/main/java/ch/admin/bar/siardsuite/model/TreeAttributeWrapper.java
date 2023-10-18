package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;


@Value
public class TreeAttributeWrapper {

    DisplayableText name;
    DisplayableText viewTitle;
    TreeContentView type;
    Optional<RenderableForm<?>> renderableForm;

    @Builder
    public TreeAttributeWrapper(
            @NonNull DisplayableText name,
            @NonNull DisplayableText viewTitle,
            @NonNull TreeContentView type,
            RenderableForm<?> renderableForm
    ) {
        this.name = name;
        this.viewTitle = viewTitle;
        this.type = type;
        this.renderableForm = Optional.ofNullable(renderableForm);
    }

    @Override
    public String toString() {
        return name.getText();
    }
}
