package ch.admin.bar.siardsuite.model.facades;

import ch.admin.bar.siard2.api.MetaParameter;

public class MetaParameterFacade {

    private MetaParameter metaParameter;

    public MetaParameterFacade(MetaParameter metaParameter) {
        this.metaParameter = metaParameter;
    }

    public String formattedCardinality() {
        if (metaParameter.getCardinality() == -1) return "";
        return String.valueOf(metaParameter.getCardinality());
    }
}
