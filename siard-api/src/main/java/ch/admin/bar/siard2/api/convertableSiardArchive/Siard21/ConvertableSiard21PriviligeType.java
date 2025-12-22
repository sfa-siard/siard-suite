package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.ConvertableSiard22PriviligeType;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.PrivilegeType;


public class ConvertableSiard21PriviligeType extends PrivilegeType {

    public ConvertableSiard21PriviligeType(PrivilegeType privilege) {
        this.type = privilege.getType();
        this.description = privilege.getDescription();
        this.grantee = privilege.getGrantee();
        this.grantor = privilege.getGrantor();
        this.object = privilege.getObject();
        this.option = privilege.getOption();
    }

    public ConvertableSiard22PriviligeType accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
