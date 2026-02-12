package ch.admin.bar.siard2.api.convertableSiardArchive.Siard10;


// understands a convertable SIARD Archive in SIARD format 1.0
public class ConvertableSiard10Archive extends ch.admin.bar.siard2.api.generated.old10.SiardArchive {

    public <T> T transform(SiardArchive10Transformer transformer) {
        return transformer.transform();
    }

}
