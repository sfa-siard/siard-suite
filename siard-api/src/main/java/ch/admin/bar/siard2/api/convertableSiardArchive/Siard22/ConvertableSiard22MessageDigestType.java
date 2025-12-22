package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.DigestTypeType;
import ch.admin.bar.siard2.api.generated.MessageDigestType;

// understand
public class ConvertableSiard22MessageDigestType extends MessageDigestType {

    ConvertableSiard22MessageDigestType(String digest, DigestTypeType digestType) {
        super();
        this.digest = digest;
        this.digestType = digestType;
    }
}
