package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Registry that manages and coordinates different value renderers.
 * Renderers are checked in priority order until one that can handle the value is found.
 */
public class ValueRendererRegistry {
    
    private final List<ValueRenderer> renderers;
    
    /**
     * Create a new registry with the given renderers.
     *
     * @param renderers the list of renderers to register
     */
    public ValueRendererRegistry(List<ValueRenderer> renderers) {
        // Sort by priority (highest first)
        this.renderers = new ArrayList<>(renderers);
        this.renderers.sort(Comparator.comparingInt(ValueRenderer::getPriority).reversed());
    }
    
    /**
     * Create a registry with default renderers for HTML export.
     *
     * @return a registry with standard HTML renderers
     */
    static ValueRendererRegistry createDefault() {
        List<ValueRenderer> defaultRenderers = List.of(
            new LobValueRenderer(),
            new UdtValueRenderer(),
            new ArrayValueRenderer(),
            new PrimitiveValueRenderer()
        );
        return new ValueRendererRegistry(defaultRenderers);
    }
    
    /**
     * Render a value using the appropriate renderer.
     *
     * @param value the value to render
     * @param context the rendering context
     * @return the HTML representation of the value
     * @throws IOException if an I/O error occurs during rendering
     * @throws IllegalArgumentException if no renderer can handle the value
     */
    String render(Value value, ValueRenderingContext context) {
        for (ValueRenderer renderer : renderers) {
            if (renderer.canRender(value)) {
                return renderer.render(value, context);
            }
        }
        
        // Fallback - should not happen with proper default renderers
        throw new IllegalArgumentException("No renderer found for value type: " + value.getClass().getSimpleName());
    }
}
