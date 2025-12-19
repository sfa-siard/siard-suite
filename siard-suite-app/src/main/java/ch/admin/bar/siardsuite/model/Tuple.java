package ch.admin.bar.siardsuite.model;

import lombok.NonNull;
import lombok.Value;

@Value
public class Tuple<T1, T2> {
    @NonNull T1 value1;
    @NonNull T2 value2;
}
