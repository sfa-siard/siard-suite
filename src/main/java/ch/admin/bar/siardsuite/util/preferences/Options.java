package ch.admin.bar.siardsuite.util.preferences;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Options {
    @NonNull Integer loginTimeout;
    @NonNull Integer queryTimeout;
}
