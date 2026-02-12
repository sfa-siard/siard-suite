package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;


import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.ConvertableSiard22RoleType;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.RoleType;

public class ConvertableSiard21RoleType extends RoleType {

    public ConvertableSiard21RoleType(RoleType role) {
        this.name = role.getName();
        this.description = role.getDescription();
        this.admin = role.getAdmin();
    }

    public ConvertableSiard22RoleType accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }


}
