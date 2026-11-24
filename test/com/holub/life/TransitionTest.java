package com.holub.life;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.awt.Rectangle;
import org.junit.jupiter.api.Test;

/**
 * transition() must report whether the cell changed, as documented in
 * {@link Cell#transition()}. Neighborhood relies on this to skip quiet blocks.
 */
class TransitionTest {

    private static final Cell DEAD = Cell.DUMMY;

    @Test
    void stableCellReportsNoChange() {
        Resident cell = new Resident();
        cell.figureNextState(DEAD, DEAD, DEAD, DEAD, DEAD, DEAD, DEAD, DEAD);

        assertFalse(cell.transition());
    }

    @Test
    void bornCellReportsChange() {
        Resident alive = liveResident();
        Resident cell = new Resident();
        cell.figureNextState(alive, alive, alive, DEAD, DEAD, DEAD, DEAD, DEAD);

        assertTrue(cell.transition());
        assertTrue(cell.isAlive());
    }

    /** A blinker inside the top-left block must not wake the bottom-right block. */
    @Test
    void farBlockStaysQuietWhileBlinkerRuns() {
        GridFixture fixture = new GridFixture().place(2, 3, "OOO");
        fixture.step(3);

        Cell farBlock = fixture.grid().edge(7, 7);
        assertTrue(farBlock.isDisruptiveTo().equals(Direction.NONE));
    }

    private static Resident liveResident() {
        Resident resident = new Resident();
        resident.userClicked(new Point(0, 0), new Rectangle(0, 0, 1, 1));
        return resident;
    }
}