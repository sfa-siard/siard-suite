package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.component.ProgressItem;

import java.util.ArrayList;
import java.util.List;

public class ProgressItems {
    private final List<ProgressItem> items = new ArrayList<ProgressItem>();

    public void add(ProgressItem item) {
        this.items.add(item);
    }

    public ProgressItem last() {
        if (this.items.size() == 0) throw new IllegalStateException("empty list! no previous progress item found!");
        return this.items.get(this.items.size() - 1);
    }
}
