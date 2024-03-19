package ch.admin.bar.siardsuite.framework.errors;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.util.ThrowingRunnable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ErrorHandler {

    private static final I18nKey UNEXPECTED_ERROR_TITLE = I18nKey.of("errors.unexpected.title");
    private static final I18nKey UNEXPECTED_ERROR_MESSAGE = I18nKey.of("errors.unexpected.message");

    private final FailureDisplay failureDisplay;
    private final List<HandlingInstruction> generalHandlingInstructions;

    public void handle(Optional<Failure> warningDefinition, Throwable throwable) {
        val definition = warningDefinition
                .orElseGet(() -> mapToFailure(throwable));

        failureDisplay.displayFailure(definition);
    }

    public void handle(final Throwable e) {
        handle(Optional.empty(), e);
    }

    public void handle(Failure failure, Throwable e) {
        handle(Optional.of(failure), e);
    }

    public void wrap(final ThrowingRunnable throwingRunnable) {
        try {
            throwingRunnable.run();
        } catch (Exception e) {
            handle(e);
        }
    }

    public Failure mapToFailure(Throwable throwable) {
        return tryFindMatchingWarningDefinition(throwable)
                .map(handlingInstruction -> Failure.builder()
                        .title(handlingInstruction.getTitle())
                        .message(handlingInstruction.getMessage())
                        .throwable(Optional.of(throwable))
                        .build())
                .orElseGet(() -> {
                    log.error("Unhandled exception", throwable);
                    return Failure.builder()
                            .title(DisplayableText.of(UNEXPECTED_ERROR_TITLE))
                            .message(DisplayableText.of(UNEXPECTED_ERROR_MESSAGE))
                            .throwable(Optional.of(throwable))
                            .build();
                });
    }

    private Optional<HandlingInstruction> tryFindMatchingWarningDefinition(final Throwable throwable) {
        val matching = generalHandlingInstructions.stream()
                .filter(handlingInstruction -> handlingInstruction.getMatcher().test(throwable))
                .findFirst();

        if (!matching.isPresent()) {
            return Optional.ofNullable(throwable.getCause())
                    .flatMap(this::tryFindMatchingWarningDefinition);
        }

        return matching;
    }
}
