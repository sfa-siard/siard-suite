package ch.admin.bar.siardsuite.component.rendered.utils;

import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Value
public class ListAssembler<T> {
    int nrOfItems;
    Function<Integer, T> itemSupplier;

    public List<T> assemble() {
        val assembledItems = new ArrayList<T>();
        for (int i = 0; i < nrOfItems; i++) {
            assembledItems.add(itemSupplier.apply(i));
        }

        return assembledItems;
    }
}
