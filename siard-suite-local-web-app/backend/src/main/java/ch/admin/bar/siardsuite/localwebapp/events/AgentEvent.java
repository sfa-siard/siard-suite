package ch.admin.bar.siardsuite.localwebapp.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * An event sent to the frontend over the {@code /api/events} stream.
 * <p>
 * Jackson writes the {@code type} property from {@link JsonSubTypes}, so records carry
 * no type field. SmallRye OpenAPI ignores the Jackson type annotations; the
 * {@link Schema} discriminator mapping lets openapi-typescript add the literal
 * {@code type} to each union member (design D6, variant 1). A new event needs an
 * entry in {@code permits}, {@link JsonSubTypes}, {@code oneOf} and the mapping.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConnectedEvent.class, name = ConnectedEvent.TYPE)
})
@Schema(oneOf = {ConnectedEvent.class},
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = ConnectedEvent.TYPE, schema = ConnectedEvent.class)
        })
public sealed interface AgentEvent permits ConnectedEvent {
}
