package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.MetaField;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siard2.api.MetaValue;
import ch.admin.bar.siard2.api.Value;
import ch.admin.bar.siard2.api.generated.CategoryType;
import lombok.SneakyThrows;

import java.io.IOException;

/**
 * Renderer for User Defined Type (UDT) values.
 * Renders UDT values as HTML definition lists with field names and values.
 */
class UdtValueRenderer implements ValueRenderer {
    
    @SneakyThrows
    @Override
    public boolean canRender(Value value) {
        MetaValue metaValue = value.getMetaValue();
        MetaType metaType = metaValue.getMetaType();
        return metaType != null && metaType.getCategoryType() == CategoryType.UDT;
    }
    
    @SneakyThrows
    @Override
    public String render(Value value, ValueRenderingContext context) {
        if (value.isNull()) return "";
        if (!hasAnyAttributes(value, context)) return "";

        MetaValue mv = value.getMetaValue();
        StringBuilder sb = new StringBuilder();
        sb.append(HtmlTemplate.definitionListStart());
        
        for (int i = 0; i < value.getAttributes(); i++) {
            MetaField mf = mv.getMetaField(i);
            sb.append(HtmlTemplate.definitionTerm(mf.getName()));
            
            // Recursively render the attribute value
            String attributeValue = context.rendererRegistry().render(value.getAttribute(i), context);
            sb.append(HtmlTemplate.definitionDescription(attributeValue));
        }
        
        sb.append(HtmlTemplate.definitionListEnd());
        return sb.toString();
    }

    private static boolean hasAnyAttributes(Value value, ValueRenderingContext context) throws IOException {
        boolean hasNonEmptyAttribute = false;

        for (int i = 0; i < value.getAttributes(); i++) {
            String attributeValue = context.rendererRegistry().render(value.getAttribute(i), context);
            if (attributeValue != null && !attributeValue.trim().isEmpty()) {
                return true;
            }
        }
        return hasNonEmptyAttribute;
    }

    @Override
    public int getPriority() {
        return 80; // High priority - UDTs should be handled before arrays
    }
}
