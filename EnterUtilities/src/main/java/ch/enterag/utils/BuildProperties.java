package ch.enterag.utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class BuildProperties
        extends Properties {
    private static final long serialVersionUID = 1L;

    private void readProperties()
            throws IOException {
        Reader rdr = new FileReader("build.properties");
        load(rdr);
        rdr.close();
    }

    public BuildProperties()
            throws IOException {
        readProperties();
    }

}
