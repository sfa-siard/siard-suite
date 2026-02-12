package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;


import ch.admin.bar.siard2.api.generated.old21.MessageDigestType;

public class ConvertableSiard21MessageDigestType extends MessageDigestType {


    public ConvertableSiard21MessageDigestType(MessageDigestType messageDigest) {
        super();
        this.digest = messageDigest.getDigest();
        this.digestType = messageDigest.getDigestType();
    }

    public ch.admin.bar.siard2.api.generated.MessageDigestType accept(Siard21Transformer visitor) {
        return visitor.visit(this);
    }
}
