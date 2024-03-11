package ch.admin.bar.siardsuite.presenter.upload.model;


import ch.admin.bar.siard2.api.Archive;
import lombok.NonNull;
import lombok.Value;

@Value
public class ArchiveAdder<T> {
    @NonNull Archive archive;
    @NonNull T data;
}
