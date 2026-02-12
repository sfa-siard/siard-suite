package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.ListAssembler;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DatabaseTable {

    private final Table table;
    private final List<DatabaseColumn> columns;

    private String description;

    public DatabaseTable(Table table) {
        this.table = table;

        val metatable = table.getMetaTable();

        description = metatable.getDescription();
        this.columns = new ListAssembler<>(metatable::getMetaColumns, metatable::getMetaColumn).assemble()
                .stream()
                .map(DatabaseColumn::new)
                .collect(Collectors.toList());
    }

    public String getName() {
        return table.getMetaTable().getName();
    }

    public long getNumberOfRows() {
        return table.getMetaTable().getRows();
    }

    public void write() {
        table.getMetaTable().setDescription(description);
    }
}
