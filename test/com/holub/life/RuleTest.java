package com.holub.life;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The rules on their own, without a grid.
 */
class RuleTest {

    @Test
    void conwayIsTheOriginalFormula() {
        for (int n = 0; n <= 8; n++) {
            assertEquals(n == 3, LifeRule.CONWAY.nextState(false, n));
            assertEquals(n == 2 || n == 3, LifeRule.CONWAY.nextState(true, n));
        }
    }

    @Test
    void highLifeIsAlsoBornWithSix() {
        assertTrue(LifeRule.HIGHLIFE.nextState(false, 6));
        assertFalse(LifeRule.CONWAY.nextState(false, 6));
    }

    @Test
    void seedsNeverSurvives() {
        for (int n = 0; n <= 8; n++)
            assertFalse(LifeRule.SEEDS.nextState(true, n));
    }
}
