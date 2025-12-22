package ch.admin.bar.siard2.cmd.utils.siard.model.utils;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

@Value(staticConstructor = "of")
public class FolderId implements Updatable<FolderId> {
    @JsonValue
    String value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public FolderId(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public FolderId applyUpdates(Updater updater) {
        return updater.applyUpdate(this);
    }
}
