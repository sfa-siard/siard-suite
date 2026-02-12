package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;


import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.SiardArchive;

// understands a convertable SIARD 2.1 archive
public class ConvertableSiard21Archive extends ch.admin.bar.siard2.api.generated.old21.SiardArchive {

    public ConvertableSiard21Archive() {
        super();
    }

    public SiardArchive accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
