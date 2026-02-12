package ch.enterag.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StopWatchTest {

    private StopWatch sw;

    @BeforeEach
    void setUp() {
        sw = new StopWatch();
    }

    @Test
    void shouldStartAndStop() throws InterruptedException {
        //given
        sw.start();
        //when
        Thread.sleep(10);
        Long Interval = sw.stop();
        //then
        assertNotNull(Interval, "Time should not be null");
    }

    @Test
    void shouldFormatLong() {
        assertEquals("1'000", StopWatch.formatLong(1000), "Formatted long is incorrect");
    }

    @Test
    void shouldFormatMs() throws InterruptedException {
        // given
        sw.start();
        //when
        Thread.sleep(10);
        sw.stop();
        //then
        assertNotNull(sw.formatMs(), "Time should not be null");
    }

    @Test
    void shouldFormatRate() throws InterruptedException {
        assertAll(
                () -> assertEquals("2.00", sw.formatRate(1000, 500), "Formatted rate is incorrect"),
                () -> assertEquals("0.00", sw.formatRate(1000, -1), "Formatted rate for negative duration is incorrect")
        );
    }

    @Test
    void shouldFormatRateWithStopWatch() throws InterruptedException {
        // given
        sw.start();
        //when
        Thread.sleep(10);
        sw.stop();
        //then
        assertTrue(sw.formatRate(1000).contains("."));
    }
}