package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.ListAssembler;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DatabaseType {

    private final MetaType metaType;
    private final List<DatabaseAttribute> databaseAttributes;

    private String description;

    public DatabaseType(MetaType metaType) {
        this.metaType = metaType;

        databaseAttributes = ListAssembler.assemble(metaType.getMetaAttributes(), metaType::getMetaAttribute)
                .stream()
                .map(DatabaseAttribute::new)
                .collect(Collectors.toList());

        description = metaType.getDescription();
    }

    public String getName() {
        return metaType.getName();
    }

    public String getCategory() {
        return metaType.getCategory();
    }

    public boolean isInstantiable() {
        return metaType.isInstantiable();
    }

    public boolean isFinal() {
        return metaType.isFinal();
    }

    public String getBase() {
        return metaType.getBase();
    }

    public void write() {
        metaType.setDescription(description);
    }
}
