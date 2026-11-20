package ch.admin.bar.siardsuite.localwebapp.connections;

import ch.admin.bar.siardsuite.localwebapp.database.DbmsRegistry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.List;

/**
 * The catalog of supported server-based DBMS for the connection picker and details form.
 * MS Access is not listed: browsers cannot provide filesystem paths (proposal).
 */
@Path("/api/dbms")
public class DbmsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Catalog of the supported server-based DBMS")
    public List<DbmsCatalogEntry> catalog() {
        return DbmsRegistry.serverBased().stream()
                .map(DbmsCatalogEntry::of)
                .toList();
    }
}
