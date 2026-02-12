package ch.admin.bar.siardsuite.framework.errors;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;
import java.util.Optional;

/**
 * Service for handling errors and mapping them into failures (which contains displayable information for users)
 */
@Slf4j
@RequiredArgsConstructor
public class ErrorHandler {

    static final I18nKey UNEXPECTED_ERROR_TITLE = I18nKey.of("errors.unexpected.title");
    static final I18nKey UNEXPECTED_ERROR_MESSAGE = I18nKey.of("errors.unexpected.message");

    private final FailureDisplay failureDisplay;
    private final List<HandlingInstruction> generalHandlingInstructions;

    /**
     * Handles a throwable by mapping it to a failure and displaying it.
     *
     * @param throwable The throwable to handle.
     */
    public void handle(final Throwable throwable) {
        val definition = mapToFailure(throwable);

        failureDisplay.displayFailure(definition);
    }

    /**
     * Maps a throwable to a failure representation.
     *
     * @param throwable The throwable to map.
     * @return The mapped failure.
     */
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

    /**
     * Tries to find a matching handling instruction for the given throwable.
     * <p>
     * It is a recursive search: If no matching {@link HandlingInstruction} is found
     * for a given {@link Throwable}, but the {@link Throwable#getCause()} does return a cause,
     * then the registered handling instructions are searched for a match with that cause.
     *
     * @param throwable The throwable to find a matching handling instruction for.
     * @return The optional matching handling instruction.
     */
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
