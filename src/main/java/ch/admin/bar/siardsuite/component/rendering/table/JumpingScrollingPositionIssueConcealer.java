package ch.admin.bar.siardsuite.component.rendering.table;

import ch.admin.bar.siardsuite.component.rendering.utils.LoadingBatchManager;
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

/**
 * This class addresses an issue in JavaFX 8 related to the table view component: The scrolling position unexpectedly jumps
 * when new items are added. This occurs only when the scrolling bar is close to its lowest position,
 * which can be triggered if the scrolling position is set to the lowest position by dragging it with the mouse cursor.
 * <p>
 * Reference:
 * <a href="https://stackoverflow.com/questions/32512654/javafx-8-stop-tableview-jumping-on-setall-call">
 * Similar issue description, but with no working solution
 * </a>
 * <p>
 * This issue is resolved in later releases of JavaFX (tested with JavaFX 17).
 * <p>
 * This class helps conceal the problematic behavior. To use it, call the {@link #concealIssue()} method
 * every time the value of a table row is changed. The class then:
 * - Checks if the scrolling bar is in an illegal state (not all data is loaded but the bar is at its lowest position).
 * - If so, sets the scrolling position to a valid index (this happens in a separate thread on a scheduled basis. Otherwise,
 * strange NullPointerExceptions are thrown by JavaFX).
 * <p>
 * TODO: Remove this workaround after upgrading the Java version. Thank you!
 */
@Slf4j
@RequiredArgsConstructor
public class JumpingScrollingPositionIssueConcealer {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    private final LoadingBatchManager loadingBatchManager;
    private final TableView<?> tableView;

    public void concealIssue() {
        if (!isScrollBarInIllegalState()) {
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
                    tableView.scrollTo((int) lastLoadingIndex);
                    tableView.refresh();
                    log.info("Scrolling position set to index {} because of trying to resolve illegal state.", lastLoadingIndex);
                }
        );
    }
}
