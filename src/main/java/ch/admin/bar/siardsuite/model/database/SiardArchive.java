package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.model.facades.ArchiveFacade;
import ch.admin.bar.siardsuite.model.facades.MetaDataFacade;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// understands the content of a SIARD Archive
public class SiardArchive {

    @Getter
    private Archive archive;
    private Optional<String> name;
    private boolean onlyMetaData = false;

    @Getter
    private List<DatabaseSchema> schemas = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Privilige> priviliges = new ArrayList<>();
    @Getter
    protected SiardArchiveMetaData metaData;

    public SiardArchive() {
    }

    public SiardArchive(
            String name,
            Archive archive,
            boolean onlyMetaData
    ) {
        this.archive = archive;
        this.onlyMetaData = onlyMetaData;
        this.name = Optional.ofNullable(name);
        metaData = new SiardArchiveMetaData(archive.getMetaData());
        this.schemas = new ArchiveFacade(archive).schemas()
                .stream()
                .map(DatabaseSchema::new)
                .collect(Collectors.toList());
        MetaDataFacade metaDataFacade = new MetaDataFacade(archive.getMetaData());
        this.users = metaDataFacade.users();
        this.priviliges = metaDataFacade.priviliges();
    }

    public Optional<String> getName() {
        return name;
    }

    public List<User> users() {
        return this.users;
    }

    public List<Privilige> priviliges() {
        return this.priviliges;
    }

    public List<DatabaseSchema> schemas() {
        return this.schemas;
    }

    public boolean onlyMetaData() {
        return this.onlyMetaData;
    }

    public void save() throws IOException {
        this.archive.saveMetaData();
        this.archive.close();
    }
}

