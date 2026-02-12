package ch.admin.bar.siard2.api.export;

/**
 * Context object that provides dependencies and configuration for value rendering.
 * This allows renderers to access shared services without tight coupling.
 */
public record ValueRenderingContext(
        LobFileHandler lobFileHandler,
        HtmlExportConfig config,
        ValueRendererRegistry rendererRegistry
) {
}
