package ch.admin.bar.siard2.api.export;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Configuration record for HTML export operations.
 * Provides customizable options for HTML generation behavior.
 * 
 * @param charset the character encoding for HTML output
 * @param dateFormat the date format pattern for date values
 * @param maxCellContentLength the maximum cell content length (-1 for unlimited)
 * @param prettifyOutput whether to format output with proper indentation
 */
record HtmlExportConfig(
    Charset charset,
    String dateFormat,
    int maxCellContentLength,
    boolean prettifyOutput
) {
    
    /**
     * Create a default configuration with sensible defaults.
     *
     * @return default HTML export configuration
     */
    static HtmlExportConfig defaultConfig() {
        return new Builder().build();
    }
    
    /**
     * Create a new builder for constructing HtmlExportConfig instances.
     *
     * @return a new builder instance
     */
    static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder class for creating HtmlExportConfig instances with fluent API.
     */
    static class Builder {
        private Charset charset = StandardCharsets.UTF_8;
        private String dateFormat = "yyyy-MM-dd";
        private int maxCellContentLength = -1; // unlimited
        private boolean prettifyOutput = true;
        
        /**
         * Set the charset for HTML output.
         *
         * @param charset the charset
         * @return this builder
         */
        Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }
        
        /**
         * Set the date format pattern.
         *
         * @param format the date format pattern
         * @return this builder
         */
        Builder dateFormat(String format) {
            this.dateFormat = format;
            return this;
        }
        
        /**
         * Set the maximum cell content length.
         *
         * @param length the maximum length (-1 for unlimited)
         * @return this builder
         */
        Builder maxCellContentLength(int length) {
            this.maxCellContentLength = length;
            return this;
        }
        
        /**
         * Set whether to prettify output with proper indentation.
         *
         * @param prettify true to prettify output
         * @return this builder
         */
        Builder prettifyOutput(boolean prettify) {
            this.prettifyOutput = prettify;
            return this;
        }
        
        /**
         * Build the configuration record.
         *
         * @return the HTML export configuration
         */
        HtmlExportConfig build() {
            return new HtmlExportConfig(charset, dateFormat, maxCellContentLength, prettifyOutput);
        }
    }
}
