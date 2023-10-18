package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaAttribute;
import lombok.Getter;
import lombok.Setter;

public class DatabaseAttribute extends DatabaseObject {

    private final MetaAttribute metaAttribute;

    @Getter
    @Setter
    private String description;

    public DatabaseAttribute(MetaAttribute metaAttribute) {
        this.metaAttribute = metaAttribute;

        this.description = metaAttribute.getDescription();
    }

    @Deprecated
    public String name() {
        return this.metaAttribute.getName();
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

    public String cardinality() {
        return String.valueOf(this.metaAttribute.getCardinality());
    }

    public void write() {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO
    }
}
