package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.Value;
import lombok.SneakyThrows;

import java.io.IOException;

/**
 * Renderer for LOB (Large Object) values that have associated file names.
 * Handles both internal and external LOBs by delegating to the LobFileHandler.
 */
class LobValueRenderer implements ValueRenderer {
    
    @SneakyThrows
    @Override
    public boolean canRender(Value value) {
        return value.getFilename() != null;
    }
    
    @SneakyThrows
    @Override
    public String render(Value value, ValueRenderingContext context) {
        String fileName = value.getFilename();
        String processedFileName = context.lobFileHandler().processLobFile(value, fileName);
        return HtmlTemplate.link(processedFileName, fileName);
    }
    
    @Override
    public int getPriority() {
        return 100; // High priority - LOBs should be handled before other checks
    }
}
