package ch.admin.bar.siard2.cmd.utils.siard.model.utils;

import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class QualifiedViewId {
    Id<Metadata.Schema> schemaId;
    Id<Metadata.View> viewId;
}
