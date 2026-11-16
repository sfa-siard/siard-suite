package ch.admin.bar.siardsuite.localwebapp.system;

import ch.admin.bar.siard2.cmd.utils.VersionsExplorer;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Collections;

/**
 * Information about the running agent (design D10). Listing the registered JDBC drivers also
 * proves that the packaged driver chain loads.
 */
@Path("/api/system")
public class SystemResource {

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Version and runtime of the agent")
    public SystemInfo info() {
        return new SystemInfo(
                VersionsExplorer.INSTANCE.getAppVersion(),
                Runtime.version().toString(),
                Collections.list(DriverManager.getDrivers()).stream()
                        .map(Driver::getClass)
                        .map(Class::getName)
                        .sorted()
                        .toList());
    }
}
