package ch.admin.bar.siard2.oracle.legacy;

import ch.enterag.utils.base.ConnectionProperties;

public class OracleConnectionProperties extends ConnectionProperties {
    private static final long serialVersionUID = 6738981594226032054L;

    public String getBFileDirectory() {
        return getProperty("dbbfiledirectory");
    }

    public String getBFileShare() {
        return getProperty("dbbfileshare");
    }

    public String getBFileFilename() {
        return getProperty("dbbfilefilename");
    }
}
