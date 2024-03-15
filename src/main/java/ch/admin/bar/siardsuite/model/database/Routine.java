package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siard2.api.MetaRoutine;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.ListAssembler;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

@Getter
@Setter
public class Routine {

    private final MetaRoutine metaRoutine;
    private final List<MetaParameter> parameters;

    private String source;
    private String body;
    private String description;

    public Routine(MetaRoutine metaRoutine) {
        this.metaRoutine = metaRoutine;
        this.parameters = ListAssembler.assemble(metaRoutine.getMetaParameters(), metaRoutine::getMetaParameter);

        this.source = metaRoutine.getSource();
        this.body = metaRoutine.getBody();
        this.description = metaRoutine.getBody();
    }

    public String getName() {
        return metaRoutine.getName();
    }

    public String getCharacteristics() {
        return metaRoutine.getCharacteristic();
    }

    public String getSpecificName() {
        return metaRoutine.getSpecificName();
    }

    public String getReturnType() {
        return metaRoutine.getReturnType();
    }

    public void write() throws IOException {
        metaRoutine.setSource(source);
        metaRoutine.setBody(body);
        metaRoutine.setDescription(description);
    }
}
