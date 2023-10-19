package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaAttribute;
import lombok.Getter;
import lombok.Setter;

public class DatabaseAttribute {

    private final MetaAttribute metaAttribute;

    @Getter
    @Setter
    private String description;

    public DatabaseAttribute(MetaAttribute metaAttribute) {
        this.metaAttribute = metaAttribute;

        this.description = metaAttribute.getDescription();
    }

    public String getName() {
        return this.metaAttribute.getName();
    }

    public int getPosition() {
        return metaAttribute.getPosition();
    }

    public String getType() {
        return metaAttribute.getType();
    }

    public String getTypeSchema() {
        return metaAttribute.getTypeSchema();
    }

    public String getTypeName() {
        return metaAttribute.getTypeName();
    }

    public String getTypeOriginal() {
        return metaAttribute.getTypeOriginal();
    }

    public boolean isNullable() {
        return metaAttribute.isNullable();
    }

    public String getDefaultValue() {
        return metaAttribute.getDefaultValue();
    }

    public int getCardinality() {
        return metaAttribute.getCardinality();
    }


    public String type() {
        return this.metaAttribute.getType();
    }

    public void write() {
        metaAttribute.setDescription(description);
    }
}
