package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.Value;

import java.io.IOException;

/**
 * Strategy interface for rendering different types of SIARD values to HTML.
 * Implementations handle specific value types (LOBs, UDTs, arrays, primitives).
 */
interface ValueRenderer {
    
    /**
     * Check if this renderer can handle the given value type.
     *
     * @param value the value to check
     * @return true if this renderer can handle the value
     */
    boolean canRender(Value value);
    
    /**
     * Render the value to HTML string.
     *
     * @param value the value to render
     * @param context the rendering context containing dependencies
     * @return the HTML representation of the value
     * @throws IOException if an I/O error occurs during rendering
     */
    String render(Value value, ValueRenderingContext context);
    
    /**
     * Get the priority of this renderer. Higher priority renderers are checked first.
     * This allows for more specific renderers to override general ones.
     *
     * @return the priority (higher numbers = higher priority)
     */
    default int getPriority() {
        return 0;
    }
}
