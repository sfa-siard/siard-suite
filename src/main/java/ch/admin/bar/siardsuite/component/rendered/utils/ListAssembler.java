package ch.admin.bar.siardsuite.component.rendered.utils;

import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Value
public class ListAssembler<T> {
    Supplier<Integer> nrOfItemsSupplier;
    Function<Integer, T> itemSupplier;

    public List<T> assemble() {
        return assemble(nrOfItemsSupplier, itemSupplier);
    }

    public static <T> List<T> assemble(final Supplier<Integer> nrOfItemsSupplier, final Function<Integer, T> itemSupplier) {
        val nrOfItems = nrOfItemsSupplier.get();
        return assemble(nrOfItems, itemSupplier);
    }

    public static <T> List<T> assemble(final int nrOfItems, final Function<Integer, T> itemSupplier) {
        val assembledItems = new ArrayList<T>();
        for (int i = 0; i < nrOfItems; i++) {
            assembledItems.add(itemSupplier.apply(i));
        }

        return assembledItems;
    }
}
