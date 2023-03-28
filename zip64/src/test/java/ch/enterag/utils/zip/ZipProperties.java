package ch.enterag.utils.zip;

import ch.enterag.utils.BuildProperties;
import ch.enterag.utils.EU;

import java.io.IOException;

public class ZipProperties
        extends BuildProperties {
    private static final long serialVersionUID = 1L;
    private static ZipProperties _zp = null;

    private ZipProperties()
            throws IOException {
        super();
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

} /* ZipProperties */
