package ch.admin.bar.siardsuite.model.facades;

import ch.enterag.sqlparser.datatype.enums.PreType;

import java.util.Arrays;
import java.util.List;

// Facade for PreTypes
public class PreTypeFacade {


    private static List<PreType> BLOB_TYPES = Arrays.asList(PreType.BLOB,
                                                            PreType.CLOB,
                                                            PreType.NCLOB,
                                                            PreType.BINARY,
                                                            PreType.VARBINARY);
    private final PreType preType;

    public PreTypeFacade(int sqlType) {
        this.preType = PreType.getBySqlType(sqlType);
    }

    public boolean isBlob() {
        return BLOB_TYPES.contains(this.preType);
    }
}
