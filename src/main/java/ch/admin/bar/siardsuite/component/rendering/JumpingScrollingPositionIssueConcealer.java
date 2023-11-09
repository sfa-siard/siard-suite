package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.component.rendering.utils.LoadingBatchManager;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import javafx.application.Platform;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class JumpingScrollingPositionIssueConcealer {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    private final AtomicBoolean scheduled = new AtomicBoolean(false);

    private final LoadingBatchManager loadingBatchManager;
    private final TableView<?> tableView;

    public void concealIssue() {
        if (!isScrollBarInIllegalState()) {
            return;
        }

        if (scheduled.getAndSet(true)) {
            // already a scheduled fix attempt -> do nothing
            return;
        }

        SCHEDULER.schedule(
                this::fixScrollPosition,
                200,
                TimeUnit.MILLISECONDS
        );
    }

    private boolean isScrollBarInIllegalState() {
        return Optional.ofNullable((ScrollBar) tableView.lookup(".scroll-bar:vertical"))
                .map(scrollBar -> {
                    val value = scrollBar.getValue();
                    val max = scrollBar.getMax();

                    return value == max && !loadingBatchManager.loadedAll();
                })
                .orElse(false);
    }

    private void fixScrollPosition() {
        val lastLoadingIndex = loadingBatchManager.getLastLoadingIndex();
        Platform.runLater(
                () -> {
                    try {
                        tableView.scrollTo((int) lastLoadingIndex);
                        tableView.refresh();
                        log.info("Scrolling position set to index {} because of trying to resolve illegal state.", lastLoadingIndex);
                    } finally {
                        scheduled.set(false);
                    }
                }
        );
    }
}
