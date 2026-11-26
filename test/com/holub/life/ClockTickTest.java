package com.holub.life;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * The clock must work without a menu bar. The original constructor built
 * the "Go" menu and failed unless MenuSite had been established.
 */
class ClockTickTest {

    @Test
    void tickNotifiesListenersWithoutMenuBar() {
        AtomicInteger ticks = new AtomicInteger();
        Clock clock = new Clock();
        clock.addClockListener(ticks::incrementAndGet);

        clock.tick();
        assertEquals(1, ticks.get());
    }
}