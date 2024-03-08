package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.presenter.tree.SiardArchiveMetaDataDetailsVisitor;
import ch.admin.bar.siardsuite.visitor.ArchiveVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Model {

    private Map<String, String> schemaMap = new HashMap<>();
    private SiardArchive siardArchive = new SiardArchive();
    public static final String TMP_SIARD = "tmp.siard";
    private Failure failure = null;

    public Model() {
    }

    public Archive initArchive() {
        try {
            return this.initArchive(File.createTempFile("tmp", ".siard"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Archive initArchive(File fileArchive, Boolean metaLoad) {
        if (fileArchive.exists()) {
            fileArchive.delete();
        }
        final Archive archive = ArchiveImpl.newInstance();
        try {
            archive.create(fileArchive);
            if (metaLoad) {
                // without tmpfiles are not deleted
                fileArchive.deleteOnExit();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return archive;
    }

    public void setSiardArchive(String name, Archive archive) {
        setSiardArchive(name, archive, false);
    }

    public void setSiardArchive(String name, Archive archive, boolean onlyMetaData) {
        this.siardArchive = new SiardArchive(name, archive, onlyMetaData);
    }

    public SiardArchive getSiardArchive() {
        if (this.siardArchive == null) this.siardArchive = new SiardArchive();
        return siardArchive;
    }

    public void provideDatabaseArchiveMetaDataProperties(SiardArchiveMetaDataDetailsVisitor visitor) {
        getSiardArchive().shareProperties(visitor);
    }

    public void provideDatabaseArchiveMetaDataObject(SiardArchiveMetaDataVisitor visitor) {
        getSiardArchive().shareObject(visitor);
    }

    public void provideArchiveProperties(ArchiveVisitor visitor) {
        getSiardArchive().shareProperties(visitor);
    }

    public void provideArchiveObject(ArchiveVisitor visitor) {
        getSiardArchive().shareObject(visitor);
    }

    public void setSchemaMap(Map schemaMap) {
        this.schemaMap = schemaMap;
    }

    public Map<String, String> getSchemaMap() {
        return schemaMap;
    }

    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    public Failure getFailure() {
        return this.failure;
    }

    public void clearSiardArchive() {
        this.siardArchive = new SiardArchive();
    }
}
