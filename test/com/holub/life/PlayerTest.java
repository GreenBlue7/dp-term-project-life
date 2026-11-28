package com.holub.life;

import static com.holub.life.LifeFiles.liveCells;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.holub.life.command.CommandHistory;
import java.awt.Point;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * The player's states, driven by hand instead of by a running timer.
 */
class PlayerTest {

    private static final Set<Point> HORIZONTAL = Set.of(new Point(20, 20), new Point(21, 20), new Point(22, 20));
    private static final Set<Point> VERTICAL = Set.of(new Point(21, 19), new Point(21, 20), new Point(21, 21));

    /** Records the requested delay instead of starting a real timer. */
    private static final class HandClock extends Clock {
        int delay;

        @Override
        public void startTicking(int millisecondsBetweenTicks) {
            delay = millisecondsBetweenTicks;
        }
    }

    private final HandClock clock = new HandClock();
    private final Universe universe = new Universe();
    private final Player player = new Player(clock, new CommandHistory(100), universe);

    PlayerTest() {
        universe.toggle(20, 20);
        universe.toggle(21, 20);
        universe.toggle(22, 20);
    }

    @Test
    void playStartsSlowAndEachPressSpeedsUp() {
        player.play();
        assertEquals(PlayerState.PLAYING, player.state());
        assertEquals(150, clock.delay);

        player.play();
        assertEquals(70, clock.delay);
    }

    @Test
    void tickMovesForwardWhilePlaying() throws Exception {
        player.play();
        clock.tick();
        assertEquals(VERTICAL, liveCells(universe));
    }

    @Test
    void rewindGoesBackAndPausesWhenHistoryRunsOut() throws Exception {
        player.play();
        clock.tick();
        clock.tick();

        player.rewind();
        clock.tick();
        assertEquals(VERTICAL, liveCells(universe));
        clock.tick();
        assertEquals(HORIZONTAL, liveCells(universe));

        clock.tick();
        assertEquals(PlayerState.PAUSED, player.state());
        assertEquals(0, clock.delay);
    }

    @Test
    void stepButtonsPauseFirst() throws Exception {
        player.play();
        player.stepForward();

        assertEquals(PlayerState.PAUSED, player.state());
        assertEquals(VERTICAL, liveCells(universe));
    }
}
