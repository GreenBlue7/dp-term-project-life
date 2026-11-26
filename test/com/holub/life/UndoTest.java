package com.holub.life;

import static com.holub.life.LifeFiles.lifeFile;
import static com.holub.life.LifeFiles.liveCells;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.holub.life.command.CommandHistory;
import java.awt.Point;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Undo on a real Universe: toggles are reversed by toggling again,
 * steps and Clear by restoring a snapshot.
 */
class UndoTest {

    private static final Set<Point> HORIZONTAL = Set.of(new Point(20, 20), new Point(21, 20), new Point(22, 20));
    private static final Set<Point> VERTICAL = Set.of(new Point(21, 19), new Point(21, 20), new Point(21, 21));

    private final Universe universe = new Universe();
    private final CommandHistory history = new CommandHistory(100);

    @Test
    void undoTogglesTheCellBack() throws Exception {
        history.execute(new ToggleCellCommand(universe, 3, 4));
        assertEquals(Set.of(new Point(3, 4)), liveCells(universe));

        history.undo();
        assertTrue(liveCells(universe).isEmpty());
    }

    @Test
    void undoStepRestoresThePreviousGeneration() throws Exception {
        universe.load(lifeFile(HORIZONTAL));
        history.execute(SnapshotCommand.step(universe));
        assertEquals(VERTICAL, liveCells(universe));

        history.undo();
        assertEquals(HORIZONTAL, liveCells(universe));
    }

    @Test
    void undoClearBringsTheCellsBack() throws Exception {
        universe.load(lifeFile(HORIZONTAL));
        history.execute(SnapshotCommand.clear(universe));

        history.undo();
        assertEquals(HORIZONTAL, liveCells(universe));
    }

    @Test
    void undoGoesBackOneActionAtATime() throws Exception {
        history.execute(new ToggleCellCommand(universe, 20, 20));
        history.execute(new ToggleCellCommand(universe, 21, 20));
        history.execute(new ToggleCellCommand(universe, 22, 20));
        history.execute(SnapshotCommand.step(universe));

        history.undo();
        assertEquals(HORIZONTAL, liveCells(universe));
        history.undo();
        assertEquals(Set.of(new Point(20, 20), new Point(21, 20)), liveCells(universe));
    }

    @Test
    void stepOnStillLifeIsNotRecorded() {
        universe.toggle(5, 5);
        universe.toggle(6, 5);
        universe.toggle(5, 6);
        universe.toggle(6, 6);
        history.execute(SnapshotCommand.step(universe));

        assertFalse(history.undo());
    }

    /** Restoring a snapshot uses the same path as Load, so after an undo a
     *  pattern on a block edge must still wake the neighboring block. */
    @Test
    void stepAfterUndoStillCrossesBlockEdges() throws Exception {
        history.execute(new ToggleCellCommand(universe, 7, 3));
        history.execute(new ToggleCellCommand(universe, 7, 4));
        history.execute(new ToggleCellCommand(universe, 7, 5));
        history.execute(SnapshotCommand.step(universe));
        history.undo();

        history.execute(SnapshotCommand.step(universe));
        assertEquals(Set.of(new Point(6, 4), new Point(7, 4), new Point(8, 4)), liveCells(universe));
    }
}
