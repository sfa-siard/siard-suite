package ch.admin.bar.siardsuite.model.facades;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siard2.api.MetaRoutine;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MetaRoutineFacade {

    private MetaRoutine metaRoutine;

    public MetaRoutineFacade(MetaRoutine metaRoutine) {
        this.metaRoutine = metaRoutine;
    }

    public Stream<MetaParameter> parameters() {
        return IntStream.range(0, metaRoutine.getMetaParameters())
                        .mapToObj(metaRoutine::getMetaParameter);
    }
}
