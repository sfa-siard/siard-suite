package ch.admin.bar.siardsuite.service.database;

import ch.enterag.utils.background.Progress;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class SiardCmdProgressListener implements Progress {

    @NonNull
    private final BiConsumer<Long, Long> onNotifyProgress;

    @Override
    public boolean cancelRequested() {
        return false;
    }

    @Override
    public void notifyProgress(int i) {
        onNotifyProgress.accept((long) i, 100L);
    }
}
