package ch.admin.bar.siard2.cmd.utils.siard.model;

import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.Content;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

@Value
public class SiardArchive implements Updatable<SiardArchive> {
    @NonNull Metadata siardMetadata;
    @NonNull Content siardContent;

    @Override
    public SiardArchive applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new SiardArchive(
                updatedThis.siardMetadata.applyUpdates(updater),
                updatedThis.siardContent.applyUpdates(updater));
    }
}
