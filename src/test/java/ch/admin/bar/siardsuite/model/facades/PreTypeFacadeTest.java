package ch.admin.bar.siardsuite.model.facades;

import ch.enterag.sqlparser.datatype.enums.PreType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class PreTypeFacadeTest {

    @Test
    void isBlob_false() {
        // given
        Arrays.stream(PreType.values()).forEach(preType -> {
                                                    if (PreType.BLOB.equals(preType) || PreType.CLOB.equals(preType) || PreType.NCLOB.equals(preType)) return;
                                                    Assertions.assertFalse(new PreTypeFacade(preType.getSqlType()).isBlob());
                                                }

        );
    }

    @Test
    void isBlob_true() {
        Assertions.assertTrue(new PreTypeFacade(PreType.BLOB.getSqlType()).isBlob());
        Assertions.assertTrue(new PreTypeFacade(PreType.CLOB.getSqlType()).isBlob());
        Assertions.assertTrue(new PreTypeFacade(PreType.NCLOB.getSqlType()).isBlob());
    }
}