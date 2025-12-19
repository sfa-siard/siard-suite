package ch.admin.bar.siard2.cmd.utils.siard.model.utils;

import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class QualifiedTypeId {
    @NonNull Id<Metadata.Schema> schemaId;
    @NonNull Id<Metadata.Type> typeId;
}
