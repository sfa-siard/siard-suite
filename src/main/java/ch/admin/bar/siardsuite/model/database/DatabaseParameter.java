package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.model.facades.MetaParameterFacade;

public class DatabaseParameter extends DatabaseObject {


    private MetaParameter metaParameter;
    private MetaParameterFacade metaParameterFacade;

    public DatabaseParameter(MetaParameter metaParameter) {
        super();
        this.metaParameter = metaParameter;
        this.metaParameterFacade = new MetaParameterFacade(metaParameter);
    }

    @Override
    public String name() {
        return metaParameter.getName();
    }

}
