package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;

import java.io.File;
import java.io.IOException;

public class Model {

    private SiardArchive siardArchive = new SiardArchive();
    public static final String TMP_SIARD = "tmp.siard";

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

    public void provideDatabaseArchiveMetaDataObject(SiardArchiveMetaDataVisitor visitor) {
        getSiardArchive().shareObject(visitor);
    }

    public void clearSiardArchive() {
        this.siardArchive = new SiardArchive();
    }
}
