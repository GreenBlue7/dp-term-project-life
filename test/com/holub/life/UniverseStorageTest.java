package com.holub.life;

import static com.holub.life.LifeFiles.entriesIn;
import static com.holub.life.LifeFiles.lifeFile;
import static com.holub.life.LifeFiles.liveCells;
import static com.holub.life.LifeFiles.store;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Load and store through Universe, without file dialogs.
 */
class UniverseStorageTest {

    @Test
    void storeThenLoadRestoresTheSameCells() throws Exception {
        Set<Point> cells = Set.of(new Point(5, 5), new Point(6, 5), new Point(40, 33));
        Universe original = new Universe();
        original.load(lifeFile(cells));

        Universe copy = new Universe();
        copy.load(new ByteArrayInputStream(store(original)));

        assertEquals(cells, liveCells(copy));
    }

    @Test
    void storedFileListsEachLiveCellOnce() throws Exception {
        Universe universe = new Universe();
        universe.load(lifeFile(Set.of(new Point(1, 1), new Point(2, 1), new Point(3, 1))));

        assertEquals(3, entriesIn(store(universe)).size());
    }

    /** The blinker sits on the east edge of the top-left block, so its next
     *  generation needs a cell born in the neighboring block. */
    @Test
    void loadedPatternOnBlockEdgeWakesTheNeighborBlock() throws Exception {
        Universe universe = new Universe();
        universe.load(lifeFile(Set.of(new Point(7, 3), new Point(7, 4), new Point(7, 5))));

        universe.nextGeneration();
        assertEquals(Set.of(new Point(6, 4), new Point(7, 4), new Point(8, 4)), liveCells(universe));
    }
}
