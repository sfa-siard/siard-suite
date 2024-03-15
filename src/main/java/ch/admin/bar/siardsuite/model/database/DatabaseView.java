package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaView;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.ListAssembler;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class DatabaseView {

    @Getter
    private final MetaView metaView;
    private final List<DatabaseColumn> columns;

    public DatabaseView(MetaView metaView) {
        this.metaView = metaView;

        this.columns = new ListAssembler<>(metaView::getMetaColumns, metaView::getMetaColumn).assemble()
                .stream()
                .map(DatabaseColumn::new)
                .collect(Collectors.toList());
    }

    public String name() {
        return metaView.getName();
    }

    public String getNumberOfColumns() {
        return String.valueOf(columns.size());
    }

    public String getNumberOfRows() {
        return String.valueOf(metaView.getRows());
    }

    public List<DatabaseColumn> columns() {
        return this.columns;
    }
}
