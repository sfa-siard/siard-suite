package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.facade.MetaTableFacade;
import ch.admin.bar.siard2.api.facade.TableRecordFacade;
import ch.admin.bar.siard2.api.primary.TableRecordDispenserImpl;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

public class HtmlExport {

    private final MetaTableFacade metaTableFacade;

    private final MetaTable metaTable;

    private final TableRecordDispenserImpl dispenser;

    private final HtmlExportConfig config;

    private final ValueRendererRegistry rendererRegistry;

    public HtmlExport(MetaTable metaTable) throws IOException {
        this(metaTable, HtmlExportConfig.defaultConfig());
    }

    public HtmlExport(MetaTable metaTable, HtmlExportConfig config) throws IOException {
        this(metaTable, config, ValueRendererRegistry.createDefault());
    }

    public HtmlExport(MetaTable metaTable, HtmlExportConfig config, ValueRendererRegistry rendererRegistry) throws IOException {
        this.metaTable = metaTable;
        this.metaTableFacade = new MetaTableFacade(metaTable);
        this.dispenser = new TableRecordDispenserImpl(metaTable.getTable());
        this.config = config;
        this.rendererRegistry = rendererRegistry;
    }

    public void write(OutputStream outputStream, File folderLobs) {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, config.charset())) {
            LobFileHandler lobHandler = new LobFileHandler(folderLobs);
            ValueRenderingContext context = new ValueRenderingContext(lobHandler, config, rendererRegistry);
            write(outputStreamWriter, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(OutputStreamWriter oswr, ValueRenderingContext context) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(HtmlTemplate.documentStart(metaTable.getName(), metaTable.getName(), metaTable.getDescription()));
        sb.append(HtmlTemplate.rowStart());
        sb.append(getColumnHeaders());
        sb.append(HtmlTemplate.rowEnd());
        sb.append(getRows(context));
        sb.append(HtmlTemplate.documentEnd());
        Document doc = Jsoup.parse(sb.toString());
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        oswr.write(doc.toString());
        oswr.flush();
    }

    private String getColumnHeaders() {
        return metaTableFacade.getMetaColums()
                              .stream()
                              .map(MetaColumn::getName)
                              .map(HtmlTemplate::tableHeader)
                              .collect(Collectors.joining());
    }

    private String getRows(ValueRenderingContext context) {
        StringBuilder sb = new StringBuilder();
        for (long rows = 0; rows < metaTable.getRows(); rows++) {
            sb.append(HtmlTemplate.rowStart());
            sb.append(getColumns(context));
            sb.append(HtmlTemplate.rowEnd());
        }
        return sb.toString();
    }

    @SneakyThrows
    private String getColumns(ValueRenderingContext context) {
        StringBuilder sb = new StringBuilder();

        new TableRecordFacade(this.dispenser.get()).getCells()
                                                   .forEach(cell -> {
                                                           sb.append(HtmlTemplate.tableCell(context.rendererRegistry()
                                                                                                   .render(cell, context)));
                                                   });

        return sb.toString();
    }

}
