package com.holub.life;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

/**
 * Drives the same 64x64 grid that Universe builds, without any Swing
 * component, so the model can be tested on its own.
 */
final class GridFixture {

    static final int SIZE = 64;

    private final Cell grid = new Neighborhood(8, new Neighborhood(8, new Resident()));
    private final Rectangle surface = new Rectangle(0, 0, SIZE, SIZE);

    /** Converts a drawing ('O' = alive, '.' = dead) into cell positions. */
    static Set<Point> pattern(int column, int row, String... lines) {
        Set<Point> cells = new HashSet<>();
        for (int y = 0; y < lines.length; y++)
            for (int x = 0; x < lines[y].length(); x++)
                if (lines[y].charAt(x) == 'O')
                    cells.add(new Point(column + x, row + y));
        return cells;
    }

    /** Toggles one cell, as a click would with one pixel per cell. */
    GridFixture toggle(int column, int row) {
        grid.userClicked(new Point(column, row), surface);
        return this;
    }

    GridFixture place(int column, int row, String... lines) {
        for (Point p : pattern(column, row, lines))
            toggle(p.x, p.y);
        return this;
    }

    /** Advances one generation the same way the clock listener in Universe does. */
    boolean step() {
        Cell d = Cell.DUMMY;
        return grid.figureNextState(d, d, d, d, d, d, d, d) && grid.transition();
    }

    void step(int generations) {
        for (int i = 0; i < generations; i++)
            step();
    }

    /** Reads the live cells through the grid's own memento. */
    Set<Point> liveCells() {
        Cell.Memento memento = (Cell.Memento) grid.createMemento();
        Set<Point> live = new HashSet<>();
        for (int row = 0; row < SIZE; row++)
            for (int column = 0; column < SIZE; column++) {
                Point p = new Point(column, row);
                if (memento.isAlive(p))
                    live.add(p);
            }
        return live;
    }

    Cell grid() {
        return grid;
    }
}