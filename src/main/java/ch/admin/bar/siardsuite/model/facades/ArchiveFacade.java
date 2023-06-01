package ch.admin.bar.siardsuite.model.facades;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArchiveFacade {

    private final Archive archive;

    public ArchiveFacade(Archive archive) {
        this.archive = archive;
    }

    public List<Schema> schemas() {
        return IntStream.range(0, this.archive.getSchemas()).mapToObj(archive::getSchema).collect(Collectors.toList());
    }
}
