package ch.admin.bar.siardsuite.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class DeactivatableListener<T> implements ChangeListener<T> {

    private final Consumer<Change<T>> listener;
    private final AtomicBoolean activated = new AtomicBoolean(true);

    public void deactivate() {
        activated.set(false);
    }

    public void activate() {
        activated.set(true);
    }

    @Override
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (activated.get() && newValue != null) {
            listener.accept(new Change<>(
                    newValue,
                    Optional.ofNullable(oldValue),
                    this));
        }
    }

    @Value
    public static class Change<T> {
        @NonNull T newValue;
        @NonNull Optional<T> oldValue;
        @NonNull DeactivatableListener<T> deactivatableListener;
    }
}
