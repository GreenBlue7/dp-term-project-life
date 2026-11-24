package com.holub.life;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Pins down what the original game already did, so that later changes
 * can be checked against it.
 */
class OriginalBehaviorTest {

    @Test
    void clickTogglesCell() {
        GridFixture fixture = new GridFixture().toggle(5, 7);
        assertEquals(Set.of(new Point(5, 7)), fixture.liveCells());

        fixture.toggle(5, 7);
        assertTrue(fixture.liveCells().isEmpty());
    }

    @Test
    void blockIsStillLife() {
        GridFixture fixture = new GridFixture().place(20, 20, "OO", "OO");
        Set<Point> before = fixture.liveCells();

        assertFalse(fixture.step());
        assertEquals(before, fixture.liveCells());
    }

    @Test
    void blinkerOscillatesWithPeriodTwo() {
        GridFixture fixture = new GridFixture().place(20, 20, "OOO");

        fixture.step();
        assertEquals(GridFixture.pattern(21, 19, "O", "O", "O"), fixture.liveCells());
        fixture.step();
        assertEquals(GridFixture.pattern(20, 20, "OOO"), fixture.liveCells());
    }

    /** Crosses several block borders, where v1.01 fixed the "stuck glider" bug. */
    @Test
    void gliderMovesAcrossBlockBorders() {
        String[] glider = {".O.", "..O", "OOO"};
        GridFixture fixture = new GridFixture().place(2, 2, glider);

        fixture.step(4 * 12);
        assertEquals(GridFixture.pattern(14, 14, glider), fixture.liveCells());
    }

    /** Cells outside the grid are the permanently dead Cell.DUMMY. */
    @Test
    void cellsBeyondTheEdgeStayDead() {
        GridFixture fixture = new GridFixture().place(10, 0, "OOO");

        fixture.step();
        assertEquals(GridFixture.pattern(11, 0, "O", "O"), fixture.liveCells());
        fixture.step();
        assertTrue(fixture.liveCells().isEmpty());
    }

    @Test
    void clearKillsEveryCell() {
        GridFixture fixture = new GridFixture().place(30, 30, "OOO").place(5, 5, "OO", "OO");

        fixture.grid().clear();
        assertTrue(fixture.liveCells().isEmpty());
        assertFalse(fixture.step());
    }
}