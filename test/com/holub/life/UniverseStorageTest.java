package com.holub.life;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Load and store through Universe, without file dialogs. A .life file is
 * a serialized collection of the live cells' positions.
 */
class UniverseStorageTest {

    @Test
    void storeThenLoadRestoresTheSameCells() throws Exception {
        Set<Point> cells = Set.of(new Point(5, 5), new Point(6, 5), new Point(40, 33));
        Universe original = new Universe();
        original.load(lifeFile(cells));

        Universe copy = new Universe();
        copy.load(new ByteArrayInputStream(store(original)));

        assertEquals(cells, new HashSet<>(cellsIn(store(copy))));
    }

    @Test
    void storedFileListsEachLiveCellOnce() throws Exception {
        Universe universe = new Universe();
        universe.load(lifeFile(Set.of(new Point(1, 1), new Point(2, 1), new Point(3, 1))));

        assertEquals(3, cellsIn(store(universe)).size());
    }

    private static InputStream lifeFile(Set<Point> cells) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(new LinkedList<>(cells));
        }
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    private static byte[] store(Universe universe) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        universe.store(bytes);
        return bytes.toByteArray();
    }

    private static List<Point> cellsIn(byte[] lifeFile) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(lifeFile))) {
            List<Point> cells = new ArrayList<>();
            for (Object cell : (Iterable<?>) in.readObject())
                cells.add((Point) cell);
            return cells;
        }
    }
}
