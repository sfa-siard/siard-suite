package ch.admin.bar.siard2.cmd.utils.siard.model.utils;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;
import lombok.val;

@Value(staticConstructor = "of")
public class Id<T> implements Updatable<Id<T>> {
    @JsonValue
    StringWrapper value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public Id(StringWrapper value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public Id<T> applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return Id.of(updatedThis.value.applyUpdates(updater));
    }

    public static <T> Id<T> of(String value) {
        return new Id<>(StringWrapper.of(value));
    }
}
