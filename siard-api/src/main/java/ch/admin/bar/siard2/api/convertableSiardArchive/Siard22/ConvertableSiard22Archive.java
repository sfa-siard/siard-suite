package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

// understands a Siard Archive v 2.2
public class ConvertableSiard22Archive extends SiardArchive {
    ConvertableSiard22Archive(String version, String dbName, String description, String archiver,
                              String archiverContact,
                              String dataOwner, String dataOriginTimespan, String lobFolder, String producerApplication,
                              XMLGregorianCalendar archivalDate,
                              String clientMachine,
                              String databaseProduct, String connection, String databaseUser,
                              List<MessageDigestType> messageDigests,
                              List<SchemaType> schemas,
                              List<UserType> users,
                              List<RoleType> roles,
                              List<PrivilegeType> privileges) {
        super();
        this.version = version;
        this.dbname = dbName;
        this.description = description;
        this.archiver = archiver;
        this.archiverContact = archiverContact;
        this.dataOwner = dataOwner;
        this.dataOriginTimespan = dataOriginTimespan;
        this.lobFolder = lobFolder;
        this.producerApplication = producerApplication;
        this.archivalDate = archivalDate;
        this.clientMachine = clientMachine;
        this.databaseProduct = databaseProduct;
        this.connection = connection;
        this.databaseUser = databaseUser;
        this.messageDigest = messageDigests;

        if (schemas.size() > 0) {
            this.schemas = new SchemasType();
            this.schemas.getSchema()
                        .addAll(schemas);
        }

        if (users.size() > 0) {
            this.users = new UsersType();
            this.users.getUser()
                      .addAll(users);
        }

        if (roles.size() > 0) {
            this.roles = new RolesType();
            this.roles.getRole()
                      .addAll(roles);
        }

        if (privileges.size() > 0) {
            this.privileges = new PrivilegesType();
            this.privileges.getPrivilege()
                           .addAll(privileges);
        }
    }
}

