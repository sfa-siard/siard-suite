package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;


@Value
public class TreeAttributeWrapper {

    String name;
    I18nKey viewTitle;
    TreeContentView type;
    Optional<RenderableForm<?>> renderableForm;

    @Builder
    public TreeAttributeWrapper(
            String name, // FIXME: NonNull?
            @NonNull I18nKey viewTitle,
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
        return name;
    }
}
