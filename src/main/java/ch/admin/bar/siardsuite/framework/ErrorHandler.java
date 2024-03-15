package ch.admin.bar.siardsuite.framework;

import ch.admin.bar.siardsuite.util.ThrowingRunnable;
import com.azul.crs.client.service.JarLoadMonitor;

public interface ErrorHandler {
    void handle(final Throwable e);

    default void wrap(final ThrowingRunnable throwingRunnable) {
        try {
            throwingRunnable.run();
        } catch (Exception e) {
            handle(e);
        }
    }
}
