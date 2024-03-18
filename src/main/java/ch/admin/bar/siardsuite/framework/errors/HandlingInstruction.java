package ch.admin.bar.siardsuite.framework.errors;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Predicate;

@Value
@Builder
public class HandlingInstruction {
    @NonNull WarningDefinition warningDefinition;
    @NonNull Predicate<Throwable> matcher;
}
