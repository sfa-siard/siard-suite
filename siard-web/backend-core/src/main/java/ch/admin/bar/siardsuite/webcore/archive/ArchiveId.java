package ch.admin.bar.siardsuite.webcore.archive;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Opaque, unguessable reference to an open archive: 16 random bytes as base64url without padding
 * (22 characters), carrying nothing about the file (design D1).
 */
public record ArchiveId(String value) {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static ArchiveId random() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return new ArchiveId(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
    }

    @Override
    public String toString() {
        return value;
    }
}
