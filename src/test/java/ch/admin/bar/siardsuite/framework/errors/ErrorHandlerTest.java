package ch.admin.bar.siardsuite.framework.errors;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;

import java.util.Arrays;
import java.util.Optional;


class ErrorHandlerTest {

    private static final HandlingInstruction HANDLE_EXCEPTION_A = HandlingInstruction.builder()
            .matcher(TypeMatcher.builder()
                    .expectedExceptionType(DummyExceptionA.class)
                    .build())
            .title(DisplayableText.of("title HANDLE_EXCEPTION_A"))
            .message(DisplayableText.of("message HANDLE_EXCEPTION_A"))
            .build();

    private static final HandlingInstruction HANDLE_EXCEPTION_B = HandlingInstruction.builder()
            .matcher(TypeMatcher.builder()
                    .expectedExceptionType(DummyExceptionB.class)
                    .build())
            .title(DisplayableText.of("title HANDLE_EXCEPTION_B"))
            .message(DisplayableText.of("message HANDLE_EXCEPTION_B"))
            .build();

    private static final String TEXT_FRAGMENT = "I am a text fragment";
    private static final HandlingInstruction HANDLE_B_WITH_EXPECTED_MESSAGE = HandlingInstruction.builder()
            .matcher(TypeAndMessageMatcher.builder()
                    .expectedExceptionType(DummyExceptionB.class)
                    .expectedTextFragment(TEXT_FRAGMENT)
                    .build())
            .title(DisplayableText.of("title HANDLE_B_WITH_EXPECTED_MESSAGE"))
            .message(DisplayableText.of("message HANDLE_B_WITH_EXPECTED_MESSAGE"))
            .build();

    @Test
    public void mapToFailure_expectDefaultFailure() {
        // given
        val handler = new ErrorHandler(
                failure -> {
                },
                Arrays.asList(
                        HANDLE_EXCEPTION_A,
                        HANDLE_EXCEPTION_B
                )
        );

        val throwable = new DummyExceptionC();

        // when
        val failure = handler.mapToFailure(throwable);

        // then
        Assertions.assertThat(failure).isEqualTo(Failure.builder()
                .title(DisplayableText.of(ErrorHandler.UNEXPECTED_ERROR_TITLE))
                .message(DisplayableText.of(ErrorHandler.UNEXPECTED_ERROR_MESSAGE))
                .throwable(Optional.of(throwable))
                .build());
    }

    @Test
    public void mapToFailure_withNestedExceptions_expectCauseOrderToWin() {
        // given
        val handler = new ErrorHandler(
                failure -> {
                },
                Arrays.asList(
                        HANDLE_EXCEPTION_A,
                        HANDLE_EXCEPTION_B
                )
        );

        // lower priority of handler, but higher priority because of cause-order
        val throwable = new DummyExceptionC(new DummyExceptionB(new DummyExceptionA()));

        // when
        val failure = handler.mapToFailure(throwable);

        // then
        Assertions.assertThat(failure).isEqualTo(Failure.builder()
                .title(HANDLE_EXCEPTION_B.getTitle())
                .message(HANDLE_EXCEPTION_B.getMessage())
                .throwable(Optional.of(throwable))
                .build());
    }

    @Test
    public void mapToFailure_withNestedExceptions_expectRegistrationOrderToWin() {
        // given
        val handler = new ErrorHandler(
                failure -> {
                },
                Arrays.asList(
                        HANDLE_B_WITH_EXPECTED_MESSAGE,
                        HANDLE_EXCEPTION_A,
                        HANDLE_EXCEPTION_B
                )
        );

        // lower priority of handler, but higher priority because of cause-order
        val throwable = new DummyExceptionC(new DummyExceptionB("i am a prefix" + TEXT_FRAGMENT + "i am a suffix", new DummyExceptionA()));

        // when
        val failure = handler.mapToFailure(throwable);

        // then
        Assertions.assertThat(failure).isEqualTo(Failure.builder()
                .title(HANDLE_B_WITH_EXPECTED_MESSAGE.getTitle())
                .message(HANDLE_B_WITH_EXPECTED_MESSAGE.getMessage())
                .throwable(Optional.of(throwable))
                .build());
    }

    private static class DummyExceptionA extends RuntimeException {
        public DummyExceptionA() {
        }

        public DummyExceptionA(Throwable cause) {
            super(cause);
        }

        public DummyExceptionA(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static class DummyExceptionB extends RuntimeException {
        public DummyExceptionB() {
        }

        public DummyExceptionB(Throwable cause) {
            super(cause);
        }

        public DummyExceptionB(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static class DummyExceptionC extends RuntimeException {
        public DummyExceptionC() {
        }

        public DummyExceptionC(Throwable cause) {
            super(cause);
        }

        public DummyExceptionC(String message, Throwable cause) {
            super(message, cause);
        }
    }


}