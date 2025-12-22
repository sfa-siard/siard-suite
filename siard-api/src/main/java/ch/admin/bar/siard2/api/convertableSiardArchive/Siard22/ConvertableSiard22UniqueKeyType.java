package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.UniqueKeyType;

import java.util.List;

public class ConvertableSiard22UniqueKeyType extends UniqueKeyType {
    public ConvertableSiard22UniqueKeyType(String name, String description, List<String> column) {
        this.name = name;
        this.description = description;
        this.column = column;
    }
}
