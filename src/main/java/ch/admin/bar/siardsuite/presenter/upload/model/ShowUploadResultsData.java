package ch.admin.bar.siardsuite.presenter.upload.model;

import ch.admin.bar.siardsuite.model.Failure;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class ShowUploadResultsData {
    @Builder.Default
    @NonNull
    Optional<Failure> failure = Optional.empty();
}