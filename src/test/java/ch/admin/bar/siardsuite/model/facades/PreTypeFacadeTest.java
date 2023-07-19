package ch.admin.bar.siardsuite.model.facades;

import ch.enterag.sqlparser.datatype.enums.PreType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreTypeFacadeTest {

    @Test
    void isBlob_false() {
        // given
        Arrays.stream(PreType.values()).forEach(preType -> {
                                                    if (PreType.BLOB.equals(preType) ||
                                                            PreType.CLOB.equals(preType) ||
                                                            PreType.NCLOB.equals(preType) ||
                                                            PreType.VARBINARY.equals(preType) ||
                                                            PreType.BINARY.equals(preType)) return;
                                                    assertFalse(new PreTypeFacade(preType.getSqlType()).isBlob());
                                                }

        );
    }

    @Test
    void isBlob_true() {
        assertTrue(new PreTypeFacade(PreType.BLOB.getSqlType()).isBlob());
        assertTrue(new PreTypeFacade(PreType.CLOB.getSqlType()).isBlob());
        assertTrue(new PreTypeFacade(PreType.NCLOB.getSqlType()).isBlob());
        assertTrue(new PreTypeFacade(PreType.BINARY.getSqlType()).isBlob());
        assertTrue(new PreTypeFacade(PreType.VARBINARY.getSqlType()).isBlob());
    }
}