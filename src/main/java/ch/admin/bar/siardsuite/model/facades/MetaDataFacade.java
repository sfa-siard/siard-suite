package ch.admin.bar.siardsuite.model.facades;

import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siardsuite.model.database.Privilige;
import ch.admin.bar.siardsuite.model.database.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetaDataFacade {

    private final MetaData metaData;

    public MetaDataFacade(MetaData metaData) {
        this.metaData = metaData;
    }

    public List<User> users() {
        return IntStream.range(0, this.metaData.getMetaUsers())
                        .mapToObj(this.metaData::getMetaUser)
                        .map(user -> new User(user.getName(), user.getDescription()))
                        .collect(Collectors.toList());
    }

    public List<Privilige> priviliges() {
        return IntStream.range(0, this.metaData.getMetaPrivileges())
                        .mapToObj(this.metaData::getMetaPrivilege)
                        .map(privilige -> new Privilige(privilige.getType(),
                                                        privilige.getObject(),
                                                        privilige.getGrantor(),
                                                        privilige.getGrantee(),
                                                        privilige.getOption(),
                                                        privilige.getDescription()))
                        .collect(Collectors.toList());
    }
}
