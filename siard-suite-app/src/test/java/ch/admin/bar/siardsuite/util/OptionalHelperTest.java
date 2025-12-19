package ch.admin.bar.siardsuite.util;

import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class OptionalHelperTest {

    private final Consumer<String> consumerMock = Mockito.mock(Consumer.class);
    private final Runnable runnableMock = Mockito.mock(Runnable.class);

    @Test
    void emptyOptional() {
        // given
        final Optional<String> emptyOptional = Optional.empty();

        // when
        OptionalHelper.when(emptyOptional)
                .isPresent(consumerMock)
                .orElse(runnableMock);

        // then
        Mockito.verify(consumerMock, Mockito.never()).accept(Mockito.any());
        Mockito.verify(runnableMock, Mockito.times(1)).run();
    }

    @Test
    void presentOptional() {
        // given
        final Optional<String> emptyOptional = Optional.of("Hi");

        // when
        OptionalHelper.when(emptyOptional)
                .isPresent(consumerMock)
                .orElse(runnableMock);

        // then
        Mockito.verify(consumerMock, Mockito.times(1)).accept(Mockito.any());
        Mockito.verify(runnableMock, Mockito.never()).run();
    }

}