package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siard2.api.MetaRoutine;
import ch.admin.bar.siardsuite.model.facades.MetaRoutineFacade;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class Routine {

    private final MetaRoutine metaRoutine;

    @Getter
    @Setter
    private String source;

    @Getter
    @Setter
    private String body;

    @Getter
    @Setter
    private String description;

    public Routine(MetaRoutine metaRoutine) {
        this.metaRoutine = metaRoutine;

        this.source = metaRoutine.getSource();
        this.body = metaRoutine.getBody();
        this.description = metaRoutine.getBody();
    }

    public String name() {
        return metaRoutine.getName();
    }

    public String characteristics() {
        return metaRoutine.getCharacteristic();
    }

    public String specificName() {
        return metaRoutine.getSpecificName();
    }

    public String returnType() {
        return metaRoutine.getReturnType();
    }

    public String numberOfParameters() {
        return String.valueOf(metaRoutine.getMetaParameters());
    }

    public List<MetaParameter> parameters() {
        return new MetaRoutineFacade(metaRoutine).parameters().collect(Collectors.toList());
    }

    public void write() {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO
    }
}
