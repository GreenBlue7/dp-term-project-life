package com.holub.life;

import static com.holub.life.LifeFiles.liveCells;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Point;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Switching the rule of a running Universe.
 */
class RuleSwitchTest {

    /** The block never changes under Conway, so its part of the grid is
     *  asleep when the rule changes. Under Seeds it must still die. */
    @Test
    void sleepingBlockFollowsTheNewRule() throws Exception {
        Universe universe = new Universe();
        universe.toggle(5, 5);
        universe.toggle(6, 5);
        universe.toggle(5, 6);
        universe.toggle(6, 6);
        universe.nextGeneration();

        universe.setRule(LifeRule.SEEDS);
        universe.nextGeneration();

        Set<Point> ring = Set.of(
                new Point(5, 4), new Point(6, 4),
                new Point(4, 5), new Point(7, 5),
                new Point(4, 6), new Point(7, 6),
                new Point(5, 7), new Point(6, 7));
        assertEquals(ring, liveCells(universe));
    }
}
