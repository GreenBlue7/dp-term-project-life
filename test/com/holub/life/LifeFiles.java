package com.holub.life;

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

/**
 * Builds and reads the original .life format (a serialized collection of
 * live-cell positions), so tests can set up and inspect a Universe.
 */
final class LifeFiles {

    private LifeFiles() {
    }

    static InputStream lifeFile(Set<Point> cells) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(new LinkedList<>(cells));
        }
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    static byte[] store(Universe universe) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        universe.store(bytes);
        return bytes.toByteArray();
    }

    /** Every entry in a .life file, duplicates included. */
    static List<Point> entriesIn(byte[] lifeFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(lifeFile))) {
            List<Point> cells = new ArrayList<>();
            for (Object cell : (Iterable<?>) in.readObject())
                cells.add((Point) cell);
            return cells;
        }
    }

    static Set<Point> liveCells(Universe universe) throws IOException, ClassNotFoundException {
        return new HashSet<>(entriesIn(store(universe)));
    }
}
