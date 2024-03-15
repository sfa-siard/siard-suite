package ch.admin.bar.siardsuite.service.preferences;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Options {
    @NonNull Integer loginTimeout;
    @NonNull Integer queryTimeout;
}
