package ch.enterag.utils.zip;

import ch.enterag.utils.EU;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class ZipProperties extends Properties {

    private static ZipProperties _zp = null;

    private ZipProperties() throws IOException {
        super();
        readProperties();
    }

    public static ZipProperties getInstance() {
        if (_zp == null) {
            try {
                _zp = new ZipProperties();
            } catch (IOException ie) {
                System.err.println(EU.getExceptionMessage(ie));
            }
        }
        return _zp;
    }

    public String getPkzipc() {
        return getProperty("pkzipc");
    }

    public String getZip30() {
        return getProperty("zip30");
    }

    public String getUnzip60() {
        return getProperty("unzip60");
    }


    private void readProperties() throws IOException {
        Reader rdr = new FileReader("test.properties");
        load(rdr);
        rdr.close();
    }

}
