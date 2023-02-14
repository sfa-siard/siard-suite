package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaData;

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
}
