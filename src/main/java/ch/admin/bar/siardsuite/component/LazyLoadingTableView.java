package ch.admin.bar.siardsuite.component;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import lombok.Value;

import java.util.LinkedHashSet;
import java.util.Optional;

public class LazyLoadingTableView<T> {

    private final TableView<T> tableView;

    LinkedHashSet<TableRow<T>> rows = new LinkedHashSet<>();

    private int firstIndex;
    private int lastIndex;

    public LazyLoadingTableView(TableView<T> tableView) {
        this.tableView = tableView;


        // Callback to monitor row creation and to identify visible screen rows
        final Callback<TableView<T>, TableRow<T>> rf = tableView.getRowFactory();

        final Callback<TableView<T>, TableRow<T>> modifiedRowFactory = new Callback<TableView<T>, TableRow<T>>() {

            @Override
            public TableRow<T> call(TableView<T> param)
            {
                TableRow<T> r = rf != null ? rf.call(param) : new TableRow<T>();
                // Save row, this implementation relies on JaxaFX re-using TableRow efficiently
                rows.add(r);
                return r;
            }
        };
        tableView.setRowFactory(modifiedRowFactory);
    }

    /**
     * Find the first row in the tableView which is visible on the display
     * @return -1 if none visible or the index of the first visible row (wholly or fully)
     */
    public int getFirstVisibleIndex()
    {
        return 0;
    }

    private void recomputeVisibleIndexes()
    {
        firstIndex = -1;
        lastIndex = -1;

        // Work out which of the rows are visible
        double tblViewHeight = tableView.getHeight();
        double headerHeight = tableView.lookup(".column-header-background").getBoundsInLocal().getHeight();
        double viewPortHeight = tblViewHeight - headerHeight;
        for(TableRow<T> r : rows)
        {
            if (!r.isVisible()) continue; // tingyik90

            double minY = r.getBoundsInParent().getMinY();
            double maxY = r.getBoundsInParent().getMaxY();

            boolean hidden  = (maxY < 0) || (minY > viewPortHeight);
            // boolean fullyVisible = !hidden && (maxY <= viewPortHeight) && (minY >= 0);
            if (!hidden)
            {
                if (firstIndex < 0 || r.getIndex() < firstIndex)
                {
                    firstIndex = r.getIndex();
                }
                if (lastIndex < 0 || r.getIndex() > lastIndex)
                {
                    lastIndex = r.getIndex();
                }
            }
        }

    }

    @Value
    public static class RowItemWrapper<T> {

        Optional<T> item;

        public boolean isLoaded() {
            return item.isPresent();
        }
    }

    public static class LazyLoadingRow<T> extends TableRow<RowItemWrapper<T>> {
        @Override
        protected void updateItem(RowItemWrapper<T> wrappedItem, boolean empty) {
            if (wrappedItem.isLoaded()) {
                super.updateItem(wrappedItem, empty);
            } else {
                // not loaded -> load

            }
        }
    }
}
